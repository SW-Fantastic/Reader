package org.swdc.reader.core.locators;

import org.jchmlib.ChmEnumerator;
import org.jchmlib.ChmFile;
import org.jchmlib.ChmStopEnumeration;
import org.jchmlib.ChmUnitInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.swdc.reader.entity.Book;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.*;

public class ChmBookLocator implements BookLocator<String> {

    private static class ChmIndexer implements ChmEnumerator {

        private String indexPath;

        private Map<String,ChmUnitInfo> infoMap = new HashMap<>();


        @Override
        public void enumerate(ChmUnitInfo chmUnitInfo) throws ChmStopEnumeration {
            String path = chmUnitInfo.getPath().toLowerCase();

            if (path.endsWith("htm") || path.endsWith("html")) {
                String name = path.substring(path.lastIndexOf("/") + 1);
                if (indexPath != null) {
                    if (name.toLowerCase().contains("index")) {
                        String oldName = indexPath.substring(indexPath.lastIndexOf("/") + 1);

                        int orgNum = oldName.length();
                        int newNum = name.length();
                        if (newNum < orgNum) {
                            indexPath = path;
                        }
                    }
                } else if (path.toLowerCase().contains("index")){
                    indexPath = path;
                }
            }

            this.infoMap.put(chmUnitInfo.getPath(),chmUnitInfo);

        }

        public String getIndexPath() {
            return indexPath;
        }
    }

    public ChmFile getChmFile() {
        return chmFile;
    }

    private Deque<String> prevHistory = new ArrayDeque<>();

    private Deque<String> nextHistory = new ArrayDeque<>();

    private ChmFile chmFile;

    private ChmIndexer indexer;

    private String url;

    private File file;

    private Boolean available = false;

    public ChmBookLocator(Book book, File assets) {
        try {

            indexer = new ChmIndexer();

            File bookFile = new File(assets.getAbsolutePath() + "/library/" + book.getName());
            this.chmFile = new ChmFile(bookFile.getAbsolutePath());
            this.chmFile.enumerate(ChmFile.CHM_ENUMERATE_ALL,indexer);

            this.file = bookFile;
            this.available = true;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ChmIndexer getIndexer() {
        return indexer;
    }

    public String prevLocation() {
        if (prevHistory.size() > 0) {
            String prev = prevHistory.removeFirst();
            nextHistory.addFirst(prev);
            url = prev;
            return toPage(url);
        }
        url = indexer.getIndexPath();
        return toPage(url);
    }


    public File getFile() {
        return file;
    }


    public String nextLocation() {
        if (nextHistory.size() > 0) {
            String next = nextHistory.removeFirst();
            prevHistory.addFirst(next);
            url = next;
            return next;
        }
        url = indexer.getIndexPath();
        return toPage(url);
    }

    @Override
    public String prevPage() {
        throw new RuntimeException("请使用prevLocation");
    }

    @Override
    public String nextPage() {
        throw new RuntimeException("请使用prevLocation");
    }


    @Override
    public String toPage(String location) {

        if (location == null && indexer.getIndexPath() == null) {
            url = chmFile.getHomeFile();
        } else if (!location.equals(url)) {
            this.prevHistory.addFirst(this.url);
            url = location;
        } else {
            url = location;
        }

        ChmUnitInfo info = indexer.infoMap.get(url);
        if (info == null) {
            return "";
        }
        ByteBuffer buffer = this.chmFile.retrieveObject(info,0,info.getLength());
        String text = new String(buffer.array());
        Document document = Jsoup.parse(text);

        Elements elements = document.select("img,a,script,link");
        for (Element element: elements) {
            String src = "";
            if (element.hasAttr("src")) {
                src = elements.attr("src");
            } else if (element.hasAttr("href")) {
                src = elements.attr("href");
            }
            if (src.startsWith("http")) {
                src = "";
            }
            if (!src.isBlank() && !src.startsWith("data")) {
                String path = Path.of(url).getParent().resolve(src).normalize()
                        .toString().replace(File.separator,"/");
                ChmUnitInfo srcInfo = chmFile.resolveObject(path);
                ByteBuffer srcData = chmFile.retrieveObject(srcInfo,0,info.getLength());
                if (element.tagName().equalsIgnoreCase("img")) {
                    String base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(srcData.array());
                    element.attr("src",base64Image);
                } else if (element.tagName().equalsIgnoreCase("script")) {
                    element.text(new String(srcData.array()));
                    element.removeAttr("src");
                } else if (element.tagName().equalsIgnoreCase("link")) {
                    if (element.hasAttr("type") && element.attr("type").equalsIgnoreCase("stylesheet")) {
                        element.text(new String(srcData.array()));
                        element.tagName("style");
                    }
                }
            }
        }
        return document.toString();
    }


    @Override
    public String getTitle() {
       return "";
    }

    @Override
    public String getLocation() {
        return url;
    }

    @Override
    public String currentPage() {
        return url;
    }

    @Override
    public void finalizeResources() {
        this.available = false;
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }

}
