package org.swdc.reader.core.locators;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import javafx.scene.text.Font;
import lombok.extern.apachecommons.CommonsLog;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.RenderResolver;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.BookProcessEvent;
import org.swdc.reader.event.ContentItemFoundEvent;
import org.swdc.reader.ui.CommonComponents;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Text文本阅读组件
 */
@CommonsLog
public class TextLocator implements BookLocator<String> {

    private File bookFile;
    private Book bookEntity;

    /**
     * 缓存页码 - 内容映射
     */
    private HashMap<Integer, String> locationTextMap = new LinkedHashMap<>();
    /**
     * 缓存页码 - 标题映射
     */
    private HashMap<Integer, String> locationTitleMap = new LinkedHashMap<>();
    private Integer currentPage = 0;

    /**
     * 用来匹配章节的正则表达式
     */
    private List<Pattern> chapterPatterns;

    /**
     * 当前适用的章节正则
     */
    private Pattern patternForChapter;

    /**
     * 当前章节名
     */
    private String chapterName = "序章";

    /**
     * 临时记录下一章节的标题
     */
    private String chapterNext=null;

    private TextConfig config;

    private BufferedReader reader;

    private String backgroundImageData;

    private Boolean available;

    private List<RenderResolver> resolvers;

    public TextLocator(List<RenderResolver> resolvers,Book book, CodepageDetectorProxy codepageDetectorProxy, TextConfig config) {
        this.resolvers = resolvers;
        File bookFile = new File("./data/library/" + book.getName());
        this.bookEntity = book;
        this.config = config;
        try {
            if (config.getEnableBackgroundImage()) {
                ByteArrayOutputStream bot = new ByteArrayOutputStream();
                DataInputStream backgroundInput = new DataInputStream(new FileInputStream(new File("configs/readerResources/text/" + config.getBackgroundImage())));
                byte[] data = new byte[1024];
                while (backgroundInput.read(data) != -1) {
                    bot.write(data);
                }
                bot.flush();
                backgroundInput.close();
                backgroundImageData = Base64.getEncoder().encodeToString(bot.toByteArray());
            }
            Charset charset = codepageDetectorProxy.detectCodepage(bookFile.toURI().toURL());
            if (charset != null) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile),charset));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile)));
            }
            this.chapterPatterns = new ArrayList<>();
            this.chapterPatterns.add(Pattern.compile("^第[^章]+章[\\s\\S]*?(?:(?=第[^章]+章)|$)"));
            this.chapterPatterns.add(Pattern.compile("^第[^回]+回[\\s\\S]*?(?:(?=回[^回]+回)|$)"));
            this.chapterPatterns.add(Pattern.compile("^第[^节]+节[\\s\\S]*?(?:(?=节[^节]+节)|$)"));
            this.chapterPatterns.add(Pattern.compile("第[^章]+章[\\s\\S]*?(?:(?=第[^章]+章)|$)"));
            available = true;
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public String prevPage() {
        this.currentPage --;
        String data = chapterAt(currentPage);
        if (data != null) {
            return data;
        } else {
            // 没有下一页，返回上一页
            this.currentPage ++;
            return chapterAt(currentPage);
        }
    }

    @Override
    public String nextPage() {
        try {
            currentPage ++;
            // 当前页内容已经存在
            String data = chapterAt(currentPage);
            if (data != null) {
                return data;
            }
            // 当前页内容不存在，读取文件并渲染样式
            if (chapterNext != null) {
                this.chapterName = chapterNext;
                chapterNext = null;
            }
            String line = chapterName.equals("序章") ? "" : chapterName;
            StringBuilder sb = new StringBuilder("<!docutype html><html><head><style>");

            for (RenderResolver resolver : resolvers) {
                if (resolver.support(this.getClass())) {
                    resolver.renderStyle(sb);
                }
            }

            sb.append("</style></head><body><div>")
                    .append("<p><h2 style=\"text-align: center\">")
                    .append(line)
                    .append("</h2></p>");
            sb.append("\n");
            readerLoop: while ((line = reader.readLine()) != null) {
                if (patternForChapter == null){
                    // 查找合用的章节正则
                    for (Pattern pattern: chapterPatterns) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            patternForChapter = pattern;
                            this.locationTitleMap.put(currentPage, chapterName);
                            this.chapterNext = line.trim();
                            break readerLoop;
                        }
                    }
                } else {
                    Matcher matcher = patternForChapter.matcher(line);
                    if (matcher.find()) {
                        this.locationTitleMap.put(currentPage, chapterName);
                        this.chapterNext = line;
                        break;
                    }
                }
                sb.append("<p>").append(line).append("</p>");
            }
            sb.append("</div></body></html>");
            locationTextMap.put(currentPage, sb.toString());
            ContentsItem item = new ContentsItem();
            item.setLocated(bookEntity);
            item.setLocation(currentPage + "");
            item.setTitle(chapterName);
            config.getApplicationConfig().publishEvent(new ContentItemFoundEvent(item));

            String content = sb.toString();
            Document document = Jsoup.parse(content);
            for (RenderResolver resolver : resolvers) {
                if (resolver.support(this.getClass())) {
                    resolver.renderContent(document.body());
                }
            }
            return document.toString();
        } catch (Exception e) {
            log.error(e);
            StringWriter swr = new StringWriter();
            e.printStackTrace(new PrintWriter(swr));
            this.currentPage --;
            this.chapterName = "读取出现异常";
            available = false;
            return swr.toString();
        }
    }

    private String chapterAt(Integer location) {
        if (locationTextMap.containsKey(location)) {
            chapterName = locationTitleMap.get(location);
            if (locationTitleMap.containsKey(location + 1)) {
                chapterNext = locationTitleMap.get(location + 1);
            }
            return locationTextMap.get(location);
        }
        return null;
    }

    @Override
    public synchronized String toPage(String location) {
        if (!this.available) {
            return null;
        }
        int page = Integer.valueOf(location);
        if (page <= 0) {
            return toPage("1");
        }
        double totals = Math.abs(page - currentPage);
        if (page > currentPage) {
            while (page > currentPage) {
                double target = 1 - ((page - currentPage) / totals);
                BookProcessEvent processEvent = new BookProcessEvent(target, "正在加载页面");
                config.getApplicationConfig().publishEvent(processEvent);
                this.nextPage();
            }
        } else if (page < currentPage) {
            while (page < currentPage) {
                double target = 1 - ((currentPage - page) / totals);
                BookProcessEvent processEvent = new BookProcessEvent(target, "正在加载页面");
                config.getApplicationConfig().publishEvent(processEvent);
                this.prevPage();
            }
        }
        BookProcessEvent processEvent = new BookProcessEvent(1.0, "");
        config.getApplicationConfig().publishEvent(processEvent);
        return currentPage();
    }

    @Override
    public String getTitle() {
        return chapterName;
    }

    @Override
    public String getLocation() {
        return currentPage + "";
    }

    @Override
    public String currentPage() {
        return locationTextMap.get(currentPage);
    }

    @Override
    public synchronized void finalizeResources() {
        try {
            if (reader!= null) {
                this.reader.close();
                this.locationTitleMap.clear();
                this.locationTextMap.clear();
                this.bookFile = null;
                this.available = false;
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }

}
