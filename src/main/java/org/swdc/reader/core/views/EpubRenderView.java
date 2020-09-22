package org.swdc.reader.core.views;

import javafx.beans.Observable;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
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
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.core.BookView;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.event.BookLocationEvent;
import org.swdc.reader.core.ext.text.TextFontFamilyAction;
import org.swdc.reader.core.ext.text.TextFontSizeAction;
import org.swdc.reader.core.ext.text.TextGapsAction;
import org.swdc.reader.core.readers.EpubReader;
import org.swdc.reader.core.readers.TextReader;

import java.awt.*;
import java.io.InputStream;

/**
 * Created by lenovo on 2019/6/13.
 */
@View(stage = false)
@CommonsLog
public class EpubRenderView extends FXView implements BookView {

    private WebView view;

    private Node tools;

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
    public Node getToolsView() {
        if (tools != null) {
            return tools;
        }
        HBox tool = new HBox();

        Button fontSizeBigger = createButton("search_plus",this::incFontSize);
        Button fontSizeMinus = createButton("search_minus",this::subFontSize);
        Button pageColumns = createButton("columns",this::toggleColumns);


        ComboBox<java.awt.Font> fontsBox = new ComboBox<>();
        fontsBox.setConverter(new StringConverter<java.awt.Font>() {
            @Override
            public String toString(java.awt.Font object) {
                if (object == null) {
                    return null;
                }
                return object.getFontName();
            }

            @Override
            public java.awt.Font fromString(String string) {
                if(string == null) {
                    return null;
                }
                return fontsBox.getItems().stream().filter(i -> i.getFontName().equals(string)).findFirst().orElse(null);
            }
        });

        fontsBox.getStyleClass().add("comb");

        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Font[] fonts = environment.getAllFonts();
        fontsBox.getItems().addAll(fonts);
        fontsBox.setPromptText("默认字体");
        fontsBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onFontChange);

        Button clean = createButton("refresh", e->{
            findComponent(TextReader.class).clearActions();
            fontsBox.getSelectionModel().clearSelection();
        });

        tool.setSpacing(8);
        tool.setPadding(new Insets(4,4,4,4));
        tool.getChildren().add(fontSizeBigger);
        tool.getChildren().add(fontSizeMinus);
        tool.getChildren().add(pageColumns);
        tool.getChildren().add(fontsBox);
        tool.getChildren().add(clean);

        tool.setAlignment(Pos.CENTER);

        this.tools = tool;
        return tool;
    }

    private void onFontChange(Observable font, java.awt.Font oldFont, java.awt.Font newFont) {
        EpubReader reader = findComponent(EpubReader.class);
        if (newFont == null) {
            reader.removeRenderAction(TextFontFamilyAction.class);
            return;
        }
        reader.addRenderAction(new TextFontFamilyAction(newFont.getFamily()));
    }

    private void incFontSize(ActionEvent event) {
        TextConfig config = findComponent(TextConfig.class);
        EpubReader reader = findComponent(EpubReader.class);
        TextFontSizeAction fontSizeAction = reader.removeRenderAction(TextFontSizeAction.class);
        if (fontSizeAction == null) {
            fontSizeAction = new TextFontSizeAction(config.getFontSize() + 2);
            reader.addRenderAction(fontSizeAction);
        } else {
            reader.addRenderAction(new TextFontSizeAction(fontSizeAction.getFontSize() + 2));
        }
    }

    private void subFontSize(ActionEvent event) {
        TextConfig config = findComponent(TextConfig.class);
        EpubReader reader = findComponent(EpubReader.class);
        TextFontSizeAction fontSizeAction = reader.removeRenderAction(TextFontSizeAction.class);
        if (fontSizeAction == null) {
            fontSizeAction = new TextFontSizeAction(config.getFontSize() - 2);
            reader.addRenderAction(fontSizeAction);
        } else {
            reader.addRenderAction(new TextFontSizeAction(fontSizeAction.getFontSize() - 2));
        }
    }

    private void toggleColumns(ActionEvent e) {
        EpubReader reader = findComponent(EpubReader.class);
        TextGapsAction gapsAction = reader.removeRenderAction(TextGapsAction.class);
        if (gapsAction == null) {
            gapsAction = new TextGapsAction();
            reader.addRenderAction(gapsAction);
        }
    }

    private Button createButton(String icon, EventHandler<ActionEvent> handler) {
        FontawsomeService fontawsomeService = findComponent(FontawsomeService.class);
        Button result = new Button();
        Font font = fontawsomeService.getFont(FontSize.MIDDLE);
        result.setFont(font);
        result.setText(fontawsomeService.getFontIcon(icon));
        result.setOnAction(handler);
        result.setPadding(new Insets(4,4,4,4));
        return result;
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
