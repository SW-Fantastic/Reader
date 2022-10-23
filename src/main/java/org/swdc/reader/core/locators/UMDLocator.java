package org.swdc.reader.core.locators;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.filetype.umd.UMDChapter;
import org.swdc.filetype.umd.UMDFile;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.BaseTextRenderResolver;
import org.swdc.reader.core.ext.RenderResolver;
import org.swdc.reader.entity.Book;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class UMDLocator implements BookLocator<String> {

    private Logger logger = LoggerFactory.getLogger(TextLocator.class);

    private List<? extends RenderResolver> resolvers;

    private Boolean available;

    private Book bookEntity;

    private TextConfig config;

    private UMDFile umdFile;

    private int currentChapter = -1;

    private Map<Integer, String> renderedTexts = new HashMap<>();

    private Map<String, String> spliters = Map.of(
            "。", "。",
            ".\\\\S+","."
    );

    public UMDLocator(List<? extends RenderResolver> resolvers, Book book, CodepageDetectorProxy codepageDetectorProxy, TextConfig config, File assets) {

        this.resolvers = resolvers;
        this.bookEntity = book;
        this.config = config;

        File bookFile = new File(assets.getAbsolutePath() + "/library/" + book.getName());
        umdFile = new UMDFile(bookFile);

        try {
            umdFile.read();
            available = true;
        } catch (Exception e) {
            available = false;
            logger.error("failed to open file");
        }

    }

    public void indexChapters(BiConsumer<String, String> tocIndexer) {
        if (this.umdFile == null) {
            return;
        }
        for (int idx = 0; idx < umdFile.getChapters().size(); idx ++) {
            tocIndexer.accept(umdFile.getChapters().get(idx).getChapterTitle(), idx + "");
        }
    }

    @Override
    public String prevPage() {
        if (currentChapter == 0) {
            return currentPage();
        }

        currentChapter --;

        StringBuilder sb = new StringBuilder("<!docutype html><html><head><style>");
        for (RenderResolver resolver : resolvers) {
            if (resolver.support(this.getClass())) {
                resolver.renderStyle(sb);
            }
        }
        sb.append("</style></head>");

        StringBuilder body = new StringBuilder("<body><div>");
        String chText = umdFile.getChapterText(currentChapter);

        String spChar = ".";
        String[] textLines = null;
        for (String sp: spliters.keySet()) {
            textLines = chText.split(sp);
            if (textLines.length > 5) {
                spChar = sp;
            }
        }

        String title = umdFile.getChapter(currentChapter).getChapterTitle();
        for (int idx = 0; idx < textLines.length; idx ++) {
            String line = textLines[idx];
            if (idx == 0 && line.contains(title)) {
                line = line.replace(title, "");
                body.append("<h2>").append(title).append("</h2>").append("<hr />");
            }
            body.append("<p>").append(line).append(spliters.get(spChar)).append("</p>");
        }
        body.append("</div></body>");
        Document document = Jsoup.parse(body.toString());
        for (RenderResolver renderResolver : resolvers) {
            if (renderResolver.support(this.getClass())) {
                renderResolver.renderContent(document.body());
            }
        }
        renderedTexts.put(currentChapter, sb.append(document.body().toString()).append("</html>").toString());
        return renderedTexts.get(currentChapter);
    }

    @Override
    public String nextPage() {
        if (currentChapter >=  umdFile.getChapters().size()) {
            return currentPage();
        }

        currentChapter ++;

        StringBuilder sb = new StringBuilder("<!docutype html><html><head><style>");
        for (RenderResolver resolver : resolvers) {
            if (resolver.support(this.getClass())) {
                resolver.renderStyle(sb);
            }
        }
        sb.append("</style></head>");

        StringBuilder body = new StringBuilder("<body><div>");
        String chText = umdFile.getChapterText(currentChapter);
        String spChar = ".";
        String[] textLines = null;
        for (String sp: spliters.keySet()) {
            textLines = chText.split(sp);
            if (textLines.length > 5) {
                spChar = sp;
            }
        }

        String title = umdFile.getChapter(currentChapter).getChapterTitle();
        for (int idx = 0; idx < textLines.length; idx ++) {
            String line = textLines[idx];
            if (idx == 0 && line.contains(title)) {
                line = line.replace(title, "");
                body.append("<h2>").append(title).append("</h2>").append("<hr />");
            }
            body.append("<p>").append(line).append(spliters.get(spChar)).append("</p>");
        }
        body.append("</div></body>");
        Document document = Jsoup.parse(body.toString());
        for (RenderResolver renderResolver : resolvers) {
            if (renderResolver.support(this.getClass())) {
                renderResolver.renderContent(document.body());
            }
        }
        renderedTexts.put(currentChapter, sb.append(document.body().toString()).append("</html>").toString());
        return renderedTexts.get(currentChapter);
    }

    @Override
    public String toPage(String location) {
        int curr = Integer.parseInt(location);
        if (curr == currentChapter) {
            return currentPage();
        }
        if (renderedTexts.containsKey(curr)) {
            return renderedTexts.get(curr);
        }
        if (curr > umdFile.getChapters().size()) {
            return "No such page";
        }

        currentChapter = curr;

        StringBuilder sb = new StringBuilder("<!docutype html><html><head><style>");
        for (RenderResolver resolver : resolvers) {
            if (resolver.support(this.getClass())) {
                resolver.renderStyle(sb);
            }
        }
        sb.append("</style></head>");

        StringBuilder body = new StringBuilder("<body><div>");
        String chText = umdFile.getChapterText(currentChapter);
        String spChar = ".";
        String[] textLines = null;
        for (String sp: spliters.keySet()) {
            textLines = chText.split(sp);
            if (textLines.length > 5) {
                spChar = sp;
            }
        }

        String title = umdFile.getChapter(currentChapter).getChapterTitle();
        for (int idx = 0; idx < textLines.length; idx ++) {
            String line = textLines[idx];
            if (idx == 0 && line.contains(title)) {
                line = line.replace(title, "");
                body.append("<h2>").append(title).append("</h2>").append("<hr />");
            }
            body.append("<p>").append(line).append(spliters.get(spChar)).append("</p>");
        }
        body.append("</div></body>");
        Document document = Jsoup.parse(body.toString());
        for (RenderResolver renderResolver : resolvers) {
            if (renderResolver.support(this.getClass())) {
                renderResolver.renderContent(document.body());
            }
        }
        renderedTexts.put(currentChapter, sb.append(document.body().toString()).append("</html>").toString());
        return renderedTexts.get(curr);
    }

    @Override
    public String getTitle() {
        return umdFile.getChapter(currentChapter).getChapterTitle();
    }

    @Override
    public String getLocation() {
        return currentChapter + "";
    }

    @Override
    public String currentPage() {
        return renderedTexts.get(currentChapter);
    }

    @Override
    public void finalizeResources() {
        available = false;
    }

    @Override
    public Boolean isAvailable() {
        return available;
    }
}
