package org.swdc.reader.core.readers;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookView;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.AbstractResolver;
import org.swdc.reader.core.ext.ExternalRenderAction;
import org.swdc.reader.core.locators.MobiLocator;
import org.swdc.reader.core.views.MobiRenderView;
import org.swdc.reader.entity.Book;
import org.swdc.reader.services.CommonComponents;

import java.util.List;

/**
 * Created by lenovo on 2019/9/29.
 */
public class MobiReader extends AbstractReader<String> {

    @Aware
    private TextConfig textConfig;

    @Aware
    private MobiRenderView view;


    @Aware
    private CommonComponents commonComponents;

    private MobiLocator locator;

    private Book book;

    @Override
    public void setBook(Book book) {
        this.book = book;
        if (this.locator != null) {
            locator.finalizeResources();
            locator = null;
        }
        try {
            List<AbstractResolver> resolvers = getScoped(AbstractResolver.class);
            locator = new MobiLocator(resolvers,commonComponents, book, textConfig);
        } catch (Exception ex) {
            logger.error("fail to init reader",ex);
        }
    }

    @Override
    public BookView getView() {
        return view;
    }

    @Override
    public Book getBook() {
        return book;
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
        return target.getName().toLowerCase().endsWith("mobi");
    }

    @Override
    public BookLocator<String> getLocator() {
        return locator;
    }
}
