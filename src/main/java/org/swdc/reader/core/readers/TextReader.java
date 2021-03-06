package org.swdc.reader.core.readers;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookView;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.AbstractResolver;
import org.swdc.reader.core.ext.ExternalRenderAction;
import org.swdc.reader.core.locators.TextLocator;
import org.swdc.reader.core.views.TextRenderView;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.CommonComponents;

import java.util.List;

/**
 * Created by lenovo on 2019/5/30.
 */
public class TextReader extends AbstractReader<String> {

    @Aware
    private TextRenderView view = null;

    @Aware
    private CommonComponents commonComponents = null;

    @Aware
    private TextConfig config = null;

    @Getter
    private BookLocator<String> locator;

    @Override
    public String getIndexedMode() {
        if (config.getDivideByChapter()) {
            return TextLocator.PAGE_BY_CHAPTER;
        } else {
            return TextLocator.PAGE_BY_COUNT;
        }
    }

    @Override
    public void setBook(Book book) {
        if (locator != null) {
            locator.finalizeResources();
            locator = null;
        }
        List<AbstractResolver> resolvers = getScoped(AbstractResolver.class);
        locator = new TextLocator(resolvers, book, commonComponents.getCodepageDetectorProxy(), config);
        this.currentBook = book;
    }

    @Override
    public BookView getView() {
        return view;
    }

    @Override
    public void renderPage(String pageData, BorderPane view) {
        WebView webView = (WebView) view.lookup("#" + this.view.getViewId());
        if (webView == null) {
            view.setCenter(this.view.getView());
            webView = (WebView) this.view.getView();
        }
        String data = pageData;
        for (ExternalRenderAction<String> action: renderActionObservableMap.values()) {
            data = action.process(data);
        }
        webView.getEngine().loadContent(data);
    }

    @Override
    protected void reload() {
        WebView webView = (WebView) getView().getView();
        String data = locator.currentPage();
        for (ExternalRenderAction<String> action: renderActionObservableMap.values()) {
            data = action.process(data);
        }
        webView.getEngine().loadContent(data);
    }

    @Override
    public boolean isSupport(Book target) {
        if (target.getName().toLowerCase().endsWith("txt") && target.getMimeData().toLowerCase().equals("text/plain")) {
            return true;
        }
        return false;
    }

}
