package org.swdc.reader.core.readers;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.locators.TextLocator;
import org.swdc.reader.core.views.TextRenderView;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/5/30.
 */
@Component
public class TextReader implements BookReader<String>{

    @Autowired
    private TextRenderView view;

    @Autowired
    private CodepageDetectorProxy encodeDescriptor;

    @Autowired
    private TextConfig config;

    @Getter
    private BookLocator<String> locator;

    private Book currentBook;

    @Override
    public void setBook(Book book) {
        if (locator != null) {
            locator.finalizeResources();
            locator = null;
        }
        locator = new TextLocator(book, encodeDescriptor, config);
        this.currentBook = book;
    }

    @Override
    public Book getBook() {
        return currentBook;
    }

    @Override
    public void renderPage(String pageData, BorderPane view) {
        WebView webView = (WebView)view.lookup("#" + this.view.getViewId());
        if (webView == null) {
            view.setCenter(this.view.getView());
            webView = (WebView)this.view.getView();
        }
        webView.getEngine().loadContent(pageData);
    }

    @Override
    public boolean isSupport(Book target) {
        if (target.getName().toLowerCase().endsWith("txt") && target.getMimeData().toLowerCase().equals("text/plain")) {
            return true;
        }
        return false;
    }

}
