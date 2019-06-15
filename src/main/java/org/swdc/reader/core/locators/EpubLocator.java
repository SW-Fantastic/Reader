package org.swdc.reader.core.locators;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import lombok.extern.apachecommons.CommonsLog;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.dom4j.*;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.ContentItemFoundEvent;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.CommonComponents;
import org.swdc.reader.utils.DataUtil;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String backgroundImageData;

    public EpubLocator(Book book, EpubConfig config, TextConfig textConfig){
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
                initContents(epubBook.getTableOfContents().getTocReferences(), config.getConfig(), "章");
            }
            fin.close();
            if (txtConfig.getEnableBackgroundImage()) {
                ByteArrayOutputStream bot = new ByteArrayOutputStream();
                DataInputStream backgroundInput = new DataInputStream(new FileInputStream(new File("configs/readerResources/text/" + textConfig.getBackgroundImage())));
                byte[] data = new byte[1024];
                while (backgroundInput.read(data) != -1) {
                    bot.write(data);
                }
                bot.flush();
                backgroundInput.close();
                backgroundImageData = Base64.getEncoder().encodeToString(bot.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initContents(List<TOCReference> toc, ApplicationConfig config, String subfix) {
        if (toc == null || toc.size() == 0) {
            return;
        }
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
            config.publishEvent(new ContentItemFoundEvent(item));
            if (ref.getChildren() != null && ref.getChildren().size() > 0) {
                this.initContents(ref.getChildren(), config, "节");
            }
        }
    }

    private Element preparImages(Document doc) throws IOException{
        List<Node> elems;
        Element elemRoot = doc.getRootElement();
        Element elem = elemRoot.element("body");
        String nsURI = elemRoot.getNamespaceURI();
        if(nsURI!=null&&!nsURI.trim().equals("")){
            HashMap<String, String> map = new HashMap<>();
            map.put("xmlns", nsURI);
            XPath x = doc.createXPath("//xmlns:img");
            x.setNamespaceURIs(map);
            elems = x.selectNodes(elem);
        }else{
            elems = elem.selectNodes("//img");
        }
        if (elems.size()>0) {
            for (Node node : elems) {
                Element element = (Element)node;
                if(element.attribute("src").getStringValue().startsWith("http")){
                    elem.remove(element);
                    continue;
                }else{
                    Resource res = epubBook.getResources().getByHref(element.attributeValue("src").replace("../", ""));
                    Base64.Encoder enc = Base64.getEncoder();
                    element.attribute("src").setValue("data:image/png;base64,"+enc.encodeToString(res.getData()));
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
        StringBuilder sb = new StringBuilder();
        sb.append("body {")
                .append("font-family: \"")
                .append(CommonComponents.getFontMap().containsKey(txtConfig.getFontPath()) ? CommonComponents.getFontMap().get(txtConfig.getFontPath()).getFamily():"Microsoft YaHei").append("\";")
                .append("font-color:").append(txtConfig.getFontColor()).append(";")
                .append("font-size:").append(txtConfig.getFontSize()).append("px;")
                .append("background-color:").append(txtConfig.getBackgroundColor()).append(";")
                .append("overflow-wrap: break-word;")
                .append("word-wrap: break-word;")
                .append("-webkit-font-smoothing: antialiased;")
                .append("padding: 18px;");
        if (txtConfig.getEnableBackgroundImage()) {
            sb.append("background-image: url(data:image/png;base64,").append(backgroundImageData).append(");");
        }
        sb.append("}")
        .append("img {")
        .append("max-width: 100%;")
        .append("}")
        .append("a {")
        .append("text-decoration: none;")
        .append("color:").append(config.getLinkColor()).append(";")
        .append("}")
        .append("li {")
        .append("padding: 12px;")
        .append("}");
        if (txtConfig.getEnableBackgroundImage()) {
            sb.append("div{")
                    .append("background-color: rgba(255,255,255,0.8);")
                    .append("padding: 36px;")
                    .append("}");
        }
        sb.append("p {")
                .append("line-height: 2;")
                .append("text-indent: ").append(txtConfig.getFontSize() *2 + "px;")
                .append("letter-spacing: 1.2px;");
        if (txtConfig.getEnableTextShadow()){
            sb.append("text-shadow: 0px 0px 5px ").append(txtConfig.getShadowColor()).append(";");
        }
        sb.append("}");
        StringBuilder scriptbbuilder = new StringBuilder();
        scriptbbuilder.append("function init() {")
                .append("var links = document.getElementsByTagName(\"a\");")
                .append("for(var idx = 0;idx < links.length; idx++) {")
                .append("links[idx].onclick = function(e){")
                .append("/*window.swdc.linkClick(this.href);*/")
                .append("swdc.locate(this.href);")
                .append("}.bind(links[idx]);")
                .append("}")
                .append("};");
        if (elem.elements("div").size() <= 0) {
            return HTML5 + "<html><head><style>" + sb.toString()
                    + "</style></head><body><div>"
                    + elem.asXML() + "</div>" +
                    "<script>" + scriptbbuilder.toString() + "</script></body></html>";
        }
        return HTML5 + "<html><head><style>" + sb.toString() + "</style></head><body>"
                + elem.asXML() + "<script>" + scriptbbuilder.toString() + "</script></body></html>";
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
                Document document = DocumentHelper.parseText(data);
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
            Document document = DocumentHelper.parseText(data);
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
            String currentLoc = epubBook.getSpine().getResource(pageIndex).getHref();
            location = DataUtil.resolveRelativePath(currentLoc, location);
            if (location.contains("#")) {
                location = location.substring(0, location.indexOf("#"));
            }
            resource = epubBook.getResources().getByHref(location);
        }
        int index = epubBook.getSpine().getResourceIndex(resource);
        try {
            String data = new String(resource.getData(),resource.getInputEncoding());
            data = this.replaceHeader(data);
            Document document = DocumentHelper.parseText(data);
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

    }

}