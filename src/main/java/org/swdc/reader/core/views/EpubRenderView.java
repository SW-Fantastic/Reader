package org.swdc.reader.core.views;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import netscape.javascript.JSObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.core.BookView;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.event.BookLocationEvent;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/6/13.
 */
@FXMLView
@CommonsLog
public class EpubRenderView extends AbstractFxmlView implements BookView {

    private BrowserView view;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private BrowserContext context;

    @Getter
    private final String viewId = "epubRenderView";

    public static class PageControl {

        private ApplicationConfig config;

        public PageControl(ApplicationConfig config){
            this.config = config;
        }

        public void locate(Object location) {
            Elements elem = Jsoup.parse(location.toString()).getElementsByTag("a");
            if (elem.size() == 1) {
                config.publishEvent(new BookLocationEvent(elem.get(0).attr("href")));
            }
        }
    }

    private PageControl control;

    @PostConstruct
    protected void initUI() {
        control = new PageControl(config);
        Platform.runLater(() ->{
            this.view = new BrowserView(new Browser(context));
            this.view.setId(viewId);
            Browser engine = this.view.getBrowser();
            engine.addLoadListener(new LoadAdapter() {
                @Override
                public void onFinishLoadingFrame(FinishLoadingEvent event) {
                    if(event.isMainFrame()) {
                        JSValue jsWindow = engine.executeJavaScriptAndReturnValue("window");
                        jsWindow.asObject().setProperty("swdc", control);
                        engine.executeJavaScript("init()");
                    }
                }
            });
            engine.addConsoleListener(consoleEvent -> {
                log.info(consoleEvent.getMessage());
            });
        });
    }

    @Override
    public void initToolsView(HBox toolBox) {
        toolBox.getChildren().clear();
    }

    @Override
    public Parent getView() {
        return this.view;
    }
}
