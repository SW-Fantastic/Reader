package org.swdc.reader.core.readers;

import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.scene.layout.BorderPane;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.RenderResolver;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.locators.MobiLocator;
import org.swdc.reader.core.views.MobiRenderView;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.ApplicationConfig;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by lenovo on 2019/9/29.
 */
@Component
@CommonsLog
public class MobiReader implements BookReader<String> {

    @Autowired
    private TextConfig textConfig;

    @Autowired
    private MobiRenderView view;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private Executor asyncExecutor;

    @Autowired
    private List<RenderResolver> resolvers;

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
            locator = new MobiLocator(resolvers,asyncExecutor,config, book, textConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public Book getBook() {
        return book;
    }

    @Override
    public void renderPage(String pageData, BorderPane view) {
        BrowserView webView = (BrowserView) view.lookup("#" + this.view.getViewId());
        if (webView == null) {
            view.setCenter(this.view.getView());
            webView = (BrowserView) this.view.getView();
        }
        webView.getBrowser().loadHTML(pageData);
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
