package org.swdc.reader.core.locators;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.fx.FXResources;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.LanguageKeys;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Text文本阅读组件
 */
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
     * 当前章节名
     */
    private String chapterName = "序章";

    /**
     * 临时记录下一章节的标题
     */
    private String chapterNext=null;

    private final int pageSize = 1200;

    private BufferedReader reader;

    private Boolean available;

    private Logger logger = LoggerFactory.getLogger(TextLocator.class);

    private List<? extends RenderResolver> resolvers;

    private Charset charset;

    private ResourceBundle resources;

    public TextLocator(ResourceBundle resources , List<? extends RenderResolver> resolvers, Book book, CodepageDetectorProxy codepageDetectorProxy, File assets) {
        this.resolvers = resolvers;
        this.resources = resources;
        File bookFile = new File(assets.getAbsolutePath() + "/library/" + book.getName());
        this.bookEntity = book;
        try {
            try {
                charset = codepageDetectorProxy.detectCodepage(bookFile.toURI().toURL());
            } catch (UnsupportedCharsetException cex) {
                if (cex.getCharsetName().equals("GB2312")) {
                    charset = Charset.forName("GBK");
                } else {
                    logger.error("unsupported charset",cex);
                    charset = Charset.defaultCharset();
                }
            }
            if (charset != null) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile),charset));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile)));
            }
            available = true;
        } catch (IOException e) {
            logger.error("error on create locator",e);
        }
    }

    @Override
    public String prevPage() {
        try {
            this.currentPage --;
            String data = chapterAt(currentPage);
            if (data != null) {
                return data;
            } else {
                // 没有下一页，返回上一页
                this.currentPage ++;
                return chapterAt(currentPage);
            }
        } catch (Exception e) {
            logger.error("fail to load prev page",e);
            StringWriter swr = new StringWriter();
            e.printStackTrace(new PrintWriter(swr));
            this.currentPage --;
            this.chapterName = resources.getString(LanguageKeys.KEY_TXT_ERROR);
            available = false;
            return swr.toString();
        }
    }

    private void reloadFile() throws Exception {
        this.currentPage = 0;
        this.locationTextMap.clear();
        this.locationTitleMap.clear();
        reader.close();
        chapterName = "序章";
        chapterNext = null;
        File bookFile = new File("./data/library/" + bookEntity.getName());
        if (charset != null) {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile),charset));
        } else {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile)));
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
            String line;
            StringBuilder sb = new StringBuilder("<!docutype html><html><head><style>");

            for (RenderResolver resolver : resolvers) {
                if (resolver.support(this.getClass())) {
                    resolver.renderStyle(sb);
                }
            }
            sb.append("</style></head><body><div>");

            int totalSize = 0;
            while (totalSize < pageSize && (line = reader.readLine()) != null) {
                totalSize = totalSize + line.length();
                sb.append("<p>").append(line).append("</p>");
                this.chapterName = resources.getString(LanguageKeys.KEY_TXT_CHAPTER)
                        .replace("#pageNo",currentPage + "");
                this.locationTitleMap.put(currentPage, this.chapterName);
            }

            sb.append("</div></body></html>");
            locationTextMap.put(currentPage, sb.toString());

            String content = sb.toString();
            Document document = Jsoup.parse(content);
            for (RenderResolver resolver : resolvers) {
                if (resolver.support(this.getClass())) {
                    resolver.renderContent(document.body());
                }
            }
            return document.toString();
        } catch (Exception e) {
            logger.error("error when load data",e);
            StringWriter swr = new StringWriter();
            e.printStackTrace(new PrintWriter(swr));
            this.currentPage --;
            this.chapterName = resources.getString(LanguageKeys.KEY_TXT_ERROR);
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
        if (page > currentPage) {
            while (page > currentPage) {
                this.nextPage();
            }
        } else if (page < currentPage) {
            while (page < currentPage) {
                this.prevPage();
            }
        }
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
            logger.error("error on finializer",e);
        }
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }

}
