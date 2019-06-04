package org.swdc.reader.core.locators;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import lombok.extern.apachecommons.CommonsLog;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.entity.Book;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
     * 当前章节名
     */
    private String chapterName = "序章";

    /**
     * 临时记录下一章节的标题
     */
    private String chapterNext=null;

    private TextConfig config;

    private BufferedReader reader;

    public TextLocator(Book book, CodepageDetectorProxy codepageDetectorProxy, TextConfig config) {
        File bookFile = new File("./data/library/" + book.getName());
        this.config = config;
        try {
            Charset charset = codepageDetectorProxy.detectCodepage(bookFile.toURI().toURL());
            if (charset != null) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile),charset));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(bookFile)));
            }
            this.chapterPatterns = new ArrayList<>();
            this.chapterPatterns.add(Pattern.compile("第[^章]+章[\\s\\S]*?(?:(?=第[^章]+章)|$)"));
            this.chapterPatterns.add(Pattern.compile("第[^回]+回[\\s\\S]*?(?:(?=回[^回]+回)|$)"));
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
        }
        return "";
    }

    @Override
    public String nextPage() {
        currentPage ++;
        try {
            // 当前页内容已经存在
            String data = chapterAt(currentPage);
            if (data != null) {
                return data;
            }
            // 当前页内容不存在
            if (chapterNext != null) {
                this.chapterName = chapterNext;
                chapterNext = null;
            } else {
                this.locationTitleMap.put(1, this.chapterName);
            }
            String line = chapterName.equals("序章") ? "" : chapterName;
            StringBuilder sb = new StringBuilder("<h2>" + line + "</h2>");
            sb.append("\n");
            readerLoop: while ((line = reader.readLine()) != null) {
                for (Pattern pattern: chapterPatterns) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        this.locationTitleMap.put(currentPage, line);
                        this.chapterNext = line;
                        break readerLoop;
                    }
                }
                sb.append("<p>").append(line).append("</p>");
            }
            locationTextMap.put(currentPage, sb.toString());
            return sb.toString();
        } catch (IOException e) {
            log.error(e);
            return "";
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
    public String toPage(String location) {
        return null;
    }

    @Override
    public String currentPage() {
        return locationTextMap.get(currentPage);
    }

    @Override
    public void finalizeResources() {
        try {
            if (reader!= null) {
                this.reader.close();
                this.locationTitleMap.clear();
                this.locationTextMap.clear();
                this.bookFile = null;
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

}
