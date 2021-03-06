package org.swdc.reader.core.readers;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.AbstractResolver;
import org.swdc.reader.core.ext.ExternalRenderAction;
import org.swdc.reader.core.locators.EpubLocator;
import org.swdc.reader.core.views.EpubRenderView;
import org.swdc.reader.entity.Book;

import java.util.List;

/**
 * Created by lenovo on 2019/6/13.
 */
public class EpubReader extends AbstractReader<String> {

    @Aware
    private EpubConfig config = null;

    @Aware
    private TextConfig textConfig = null;

    @Aware
    private EpubRenderView view = null;

    private EpubLocator locator;

    @Override
    public void setBook(Book book) {
        if (locator != null) {
            locator.finalizeResources();
            locator = null;
        }
        this.currentBook = book;
        List<AbstractResolver> resolvers = getScoped(AbstractResolver.class);
        locator = new EpubLocator(resolvers,book, config, textConfig);
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
        return target.getName().endsWith("epub") &&
                target.getMimeData().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    @Override
    public EpubRenderView getView() {
        return view;
    }

    @Override
    public BookLocator<String> getLocator() {
        return locator;
    }
}
