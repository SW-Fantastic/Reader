package org.swdc.reader.core.locators;

import org.jchmlib.ChmEnumerator;
import org.jchmlib.ChmFile;
import org.jchmlib.ChmStopEnumeration;
import org.jchmlib.ChmUnitInfo;
import org.swdc.reader.core.URLManager;
import org.swdc.reader.entity.Book;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Permission;
import java.util.*;

public class ChmBookLocator implements BookLocator<String> {

    private static ChmURLHandler handler = new ChmURLHandler();

    static {
        URLManager.register("vchm",handler);
    }

    public static class ChmURLConnection extends URLConnection {

        private ChmFile chmFile;

        private String path;

        public ChmURLConnection(URL url, ChmFile file,String path) {
            super(url);
            this.chmFile = file;
            this.path = path;
        }

        @Override
        public void connect() throws IOException {

        }

        @Override
        public InputStream getInputStream() throws IOException {
            ChmUnitInfo info = chmFile.resolveObject(path);
            if (info == null) {
                return InputStream.nullInputStream();
            }
            ByteBuffer buffer = this.chmFile.retrieveObject(info,0,info.getLength());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(buffer.array());

            ByteArrayInputStream bin = new ByteArrayInputStream(outputStream.toByteArray());

            return bin;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return OutputStream.nullOutputStream();
        }

        @Override
        public Permission getPermission() throws IOException {
            return null;
        }
    }

    private static class ChmURLHandler implements URLManager.URLHandler {

        @Override
        public URLStreamHandler accept() {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    String realPath = u.getHost().substring(0,u.getHost().lastIndexOf("|"));
                    String path = new String(Base64.getDecoder().decode(realPath.getBytes(StandardCharsets.UTF_8)));

                    File chm = new File(path);
                    if (!chm.exists()) {
                        throw new FileNotFoundException(chm.getAbsolutePath() + "不存在");
                    }
                    ChmFile file = new ChmFile(chm.getAbsolutePath());
                    URL contentURL = new URL("vchm://" + u.getPath());

                    return new ChmURLConnection(contentURL,file,u.getPath());
                }
            };
        }

    }

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
            System.err.println(indexPath);

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
            return location();
        }
        url = indexer.getIndexPath();
        return location();
    }


    public void location(String location) {
        if (!location.equals(url)) {
            this.prevHistory.addFirst(this.url);
        }
        url = location;
    }

    public File getFile() {
        return file;
    }

    public String location() {
        if (url == null && indexer.getIndexPath() == null) {
            url = chmFile.getHomeFile();
        }
        return "vchm://" + Base64.getEncoder()
                .encodeToString(file.getAbsolutePath().getBytes(StandardCharsets.UTF_8))
                + "|" +  url;
    }

    public String nextLocation() {
        if (nextHistory.size() > 0) {
            String next = nextHistory.removeFirst();
            prevHistory.addFirst(next);
            url = next;
            return next;
        }
        url = indexer.getIndexPath();
        return location();
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
        throw new RuntimeException("此功能不可用");
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
