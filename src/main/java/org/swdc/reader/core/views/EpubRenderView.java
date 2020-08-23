package org.swdc.reader.core.views;

import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.swdc.fx.AppComponent;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.reader.core.BookView;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.event.BookLocationEvent;

import java.io.InputStream;

/**
 * Created by lenovo on 2019/6/13.
 */
@View(stage = false)
@CommonsLog
public class EpubRenderView extends FXView implements BookView {

    private WebView view;

    @Getter
    private final String viewId = "epubRenderView";

    @Aware
    private EpubConfig config = null;

    private static String pageScript = null;

    public static class PageControl {

        private AppComponent source;

        public PageControl(AppComponent source){
            this.source = source;
        }

        public void locate(Object location) {
            Elements elem = Jsoup.parse(location.toString()).getElementsByTag("a");
            if (elem.size() == 1) {
                source.emit(new BookLocationEvent(elem.get(0).attr("href"),source));
            }
        }
    }

    private PageControl control;

    @Override
    public void initToolsView(HBox toolBox) {
        toolBox.getChildren().clear();
    }

    @Override
    public Parent getView() {
        return this.view;
    }

    @Override
    protected Parent create() {
        control = new PageControl(this);
        this.view = new WebView();
        this.view.setId(viewId);
        WebEngine engine = this.view.getEngine();
        engine.setJavaScriptEnabled(true);
        engine.setOnError(event -> {
            event.getException().printStackTrace();
            log.error(event.getException());
        });
        engine.setOnAlert(event -> {
            this.showAlertDialog("提示",event.getData(), Alert.AlertType.INFORMATION);
        });
        engine.getLoadWorker().stateProperty().addListener((observableValue, state, stateNew) -> {
            if (stateNew == Worker.State.SUCCEEDED && config.getEnableHyperLinks()) {
                JSObject jsObject = (JSObject) engine.executeScript("window");
                jsObject.setMember("swdc", control);
                if (config.getEnableHyperLinks()) {
                    try {
                        if (pageScript == null) {
                            InputStream in = this.getClass().getModule().getResourceAsStream("views/EpubView.js");
                            pageScript = IOUtils.toString(in,"utf8");
                        }
                        engine.executeScript(pageScript);
                    } catch (Exception e) {
                        logger.error("can not load javascript for web page",e);
                    }

                }
            }
        });
        return this.view;
    }
}
