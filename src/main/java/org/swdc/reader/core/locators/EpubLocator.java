package org.swdc.reader.core.locators;

import lombok.extern.apachecommons.CommonsLog;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.RenderResolver;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.event.BookProcessEvent;
import org.swdc.reader.core.event.ContentItemFoundEvent;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.utils.DataUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2019/6/13.
 */
@CommonsLog
public class EpubLocator implements BookLocator<String> {

    private Book book;
    private EpubConfig config;
    private TextConfig txtConfig;
    private EpubReader reader;

    private Map<String, Integer> locationIndexMap;
    private Map<Integer, String> indexTitleMap;
    private Map<Integer, String> indexContentMap;

    private nl.siegmann.epublib.domain.Book epubBook;

    private Integer pageIndex = -1;

    private static final String HTML5 = "<!DOCTYPE HTML>";

    private static final String HTML_DTD_11 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">";
    private static final String HTML_DTD_10T = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    private static final String HTML_DTD_10F = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">";
    private static final String HTML_DTD_10S = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
    private static final String HTML_DTD_401F = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">";
    private static final String HTML_DTD_401T = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";
    private static final String HTML_DTD_401S = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";

    private Boolean availbale;

    private List<? extends RenderResolver> resolvers;

    public EpubLocator(List<? extends RenderResolver> resolvers, Book book, EpubConfig config, TextConfig textConfig){
        this.resolvers = resolvers;
        this.book = book;
        this.config = config;
        this.txtConfig = textConfig;
        File bookFile = new File("./data/library/" + book.getName());
        this.reader = new EpubReader();
        indexContentMap = new LinkedHashMap<>();
        locationIndexMap = new LinkedHashMap<>();
        indexTitleMap = new LinkedHashMap<>();
        try {
            FileInputStream fin = new FileInputStream(bookFile);
            epubBook = reader.readEpub(fin);
            if (book.getContentsItems() == null || book.getContentsItems().size() == 0) {
                initContents(epubBook.getTableOfContents().getTocReferences(), "章",true);
            }
            fin.close();
            this.availbale = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initContents(List<TOCReference> toc, String subfix, boolean isOuther) {

        if (toc == null || toc.size() == 0) {
            return;
        }
        double totals = toc.size();
        for (int idx = 0; idx < toc.size(); idx++) {
            TOCReference ref = toc.get(idx);
            ContentsItem item = new ContentsItem();
            String title = ref.getTitle();
            if (title == null || title.trim().equals("")) {
                title = "第" + idx + subfix;
            }
            item.setTitle(title);
            item.setLocation(ref.getResource().getHref());
            item.setLocated(book);
            config.emit(new ContentItemFoundEvent(item,config));
            double progress = 1 - (idx/totals);
            BookProcessEvent processEvent = new BookProcessEvent(progress, "正在索引目录：" + title,config);
            config.emit(processEvent);
            if (ref.getChildren() != null && ref.getChildren().size() > 0) {
                this.initContents(ref.getChildren(), "节",false);
            }
            processEvent = new BookProcessEvent(isOuther ? 1.0 : 0.8, isOuther ? "完成": "正在索引目录：" + title,config);
            config.emit(processEvent);
        }
    }

    private Element preparImages(Document doc) throws IOException {
        Element elem = doc.body();

        List<Element> elems = elem.getElementsByTag("img");

        if (elems.size()>0) {
            for (Element element : elems) {
                if(element.attr("src").startsWith("http")){
                    element.parent().children().remove(element);
                    continue;
                }else{
                    String path = epubBook.getSpine().getResource(pageIndex).getHref();
                    Resource res = epubBook.getResources().getByHref(DataUtil.resolveRelativePath(path, element.attr("src")));
                    Base64.Encoder enc = Base64.getEncoder();
                    element.attr("src", "data:image/png;base64,"+enc.encodeToString(res.getData()));
                }
            }
        }
        return elem;
    }

    private String replaceHeader(String pageStr){
        pageStr = pageStr.replace("\n", "").replace("\r", "").trim();
        pageStr = pageStr.replace("&nbsp", " ");
        pageStr = pageStr.replace(HTML_DTD_11.trim(), HTML5);
        pageStr = pageStr.replace(HTML_DTD_10F.trim(), HTML5);
        pageStr = pageStr.replace(HTML_DTD_10T.trim(), HTML5);
        pageStr = pageStr.replace(HTML_DTD_10S.trim(), HTML5);
        pageStr = pageStr.replace(HTML_DTD_401F.trim(), HTML5);
        pageStr = pageStr.replace(HTML_DTD_401S.trim(), HTML5);
        pageStr = pageStr.replace(HTML_DTD_401T.trim(), HTML5);
        return pageStr;
    }

    private String renderPage(Element elem) {
        Element element = elem;
        element.getElementsByTag("a").forEach(link -> {
            if (link.text().length() > 20) {
                link.tagName("p");
                link.removeAttr("href");
            }
        });
        StringBuilder sb = new StringBuilder();
        for (RenderResolver resolver : resolvers) {
            if (resolver.support(this.getClass())) {
                resolver.renderStyle(sb);
                resolver.renderContent(elem);
            }
        }
        return HTML5 + "<html><head><style>" + sb.toString()
                    + "</style></head><body><div>"
                    + element.html() + "</div>" +
                    "</body></html>";
    }

    @Override
    public String prevPage() {
        this.pageIndex --;
        if (this.pageIndex >= 0 && indexContentMap.containsKey(this.pageIndex)) {
            return indexContentMap.get(this.pageIndex);
        } else if (pageIndex < 0) {
            return nextPage();
        } else {
            Spine chapterList = epubBook.getSpine();
            try {
                Resource resource = chapterList.getResource(pageIndex);
                String data = new String(resource.getData(),resource.getInputEncoding());
                data = this.replaceHeader(data);
                Document document = Jsoup.parse(data);
                String location = resource.getHref();
                data = renderPage(preparImages(document));
                locationIndexMap.put(location, pageIndex);
                indexContentMap.put(pageIndex, data);
                return data;
            } catch (Exception e){
                return "No such page.";
            }
        }
    }

    @Override
    public String nextPage() {
        this.pageIndex ++;
        if (indexContentMap.containsKey(pageIndex)) {
            return indexContentMap.get(pageIndex);
        }
        Spine chapterList = epubBook.getSpine();
        try {
            Resource resource = chapterList.getResource(pageIndex);
            String data = new String(resource.getData(),resource.getInputEncoding());
            data = this.replaceHeader(data);
            Document document = Jsoup.parse(data);
            data = renderPage(preparImages(document));

            String location = resource.getHref();
            locationIndexMap.put(location, pageIndex);
            indexContentMap.put(pageIndex, data);
            return data;
        } catch (Exception e) {
            if (pageIndex > epubBook.getContents().size()) {
                return prevPage();
            }
            return e.getClass().getName() + "caused by nextPage Method.";
        }
    }

    @Override
    public String toPage(String location) {
        Resource resource;
        try {
            Integer idx = Integer.valueOf(location);
            if (indexContentMap.containsKey(idx)) {
                this.pageIndex = idx;
                return indexContentMap.get(idx);
            }
            resource = epubBook.getSpine().getResource(idx);
        } catch (Exception e) {
            if (locationIndexMap.containsKey(location)) {
                pageIndex = locationIndexMap.get(location);
                return indexContentMap.get(pageIndex);
            }
            // 处理相对路径
            if (location.contains("#")) {
                location = location.substring(0, location.indexOf("#"));
            }
            resource = epubBook.getResources().getByHref(location);
            if (resource == null){
                String currentLoc = epubBook.getSpine().getResource(pageIndex).getHref();
                location = DataUtil.resolveRelativePath(currentLoc, location);
                resource = epubBook.getResources().getByHref(location);
            }
        }
        int index = epubBook.getSpine().getResourceIndex(resource);
        try {
            String data = new String(resource.getData(),resource.getInputEncoding());
            data = this.replaceHeader(data);
            Document document = Jsoup.parse(data);
            data = renderPage(preparImages(document));

            locationIndexMap.put(location, index);
            indexContentMap.put(index,data);
            this.pageIndex = index;
            return data;
        } catch (Exception e) {
            return e.getClass().getName() + "caused by toPage Method.";
        }
    }

    @Override
    public String getTitle() {
        Spine spline = epubBook.getSpine();
        Resource res = spline.getResource(pageIndex);
        return "第" + spline.getResourceIndex(res) + "章";
    }

    @Override
    public String getLocation() {
        return pageIndex + "";
    }

    @Override
    public String currentPage() {
        return indexContentMap.get(pageIndex);
    }

    @Override
    public void finalizeResources() {
        this.availbale = false;
    }

    @Override
    public Boolean isAvailable() {
        return availbale;
    }

}
