package org.swdc.reader.core.readers;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.swdc.reader.core.BookLocator;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.locators.TextLocator;
import org.swdc.reader.core.views.WebRenderView;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/5/30.
 */
@Component
public class TextReader implements BookReader<String>{


    @Autowired
    private WebRenderView view;

    @Getter
    private BookLocator<String> locator;

    @Override
    public void setBook(Book book) {
        if (locator != null) {
            locator = null;
        }
        locator = new TextLocator(book);
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
