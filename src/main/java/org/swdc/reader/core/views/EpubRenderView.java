package org.swdc.reader.core.views;

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

    private WebView view;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private EpubConfig epubConfig;

    @Getter
    private final String viewId = "epubRenderView";

    public static class PageControl {

        private ApplicationConfig config;

        public PageControl(ApplicationConfig config){
            this.config = config;
        }

        public void locate(Object location) {
           config.publishEvent(new BookLocationEvent(location.toString()));
        }
    }

    private PageControl control;

    @PostConstruct
    protected void initUI() {
        control = new PageControl(config);
        Platform.runLater(() ->{
            this.view = new WebView();
            this.view.setId(viewId);
            WebEngine engine = this.view.getEngine();
            engine.setOnError(event -> {
                log.error(event.getException());
            });
            engine.setOnAlert(event -> {
                UIUtils.showAlertDialog(event.getData(),"提示", Alert.AlertType.INFORMATION,config);
            });
            engine.setJavaScriptEnabled(true);
            engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED && epubConfig.getEnableHyperLinks()) {
                    JSObject jsObject = (JSObject) engine.executeScript("window");
                    jsObject.setMember("swdc", control);
                    try {
                        engine.executeScript("init()");
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
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
