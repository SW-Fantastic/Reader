package org.swdc.reader.core.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.core.BookView;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.core.ext.text.TextFontSizeAction;
import org.swdc.reader.core.ext.text.TextGapsAction;
import org.swdc.reader.core.readers.TextReader;



/**
 * Created by lenovo on 2019/5/31.
 */
@View(stage = false)
public class TextRenderView extends FXView implements BookView {

    private WebView view;

    @Getter
    private final String viewId = "webRenderView";

    private Node tools;

    @Override
    public Parent getView() {
        return this.view;
    }

    @Override
    protected Parent create() {
        this.view = new WebView();
        this.view.setId(viewId);
        return view;
    }

    @Override
    public Node getToolsView() {
        if (tools != null) {
            return tools;
        }
        HBox tool = new HBox();

        Button fontSizeBigger = createButton("search_plus",this::incFontSize);
        Button fontSizeMinus = createButton("search_minus",this::subFontSize);
        Button pageColumns = createButton("columns",this::toggleColumns);

        tool.setSpacing(8);
        tool.setPadding(new Insets(4,4,4,4));
        tool.getChildren().add(fontSizeBigger);
        tool.getChildren().add(fontSizeMinus);
        tool.getChildren().add(pageColumns);
        this.tools = tool;
        return tool;
    }

    private void incFontSize(ActionEvent event) {
        TextConfig config = findComponent(TextConfig.class);
        TextReader reader = findComponent(TextReader.class);
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
        TextReader reader = findComponent(TextReader.class);
        TextFontSizeAction fontSizeAction = reader.removeRenderAction(TextFontSizeAction.class);
        if (fontSizeAction == null) {
            fontSizeAction = new TextFontSizeAction(config.getFontSize() - 2);
            reader.addRenderAction(fontSizeAction);
        } else {
            reader.addRenderAction(new TextFontSizeAction(fontSizeAction.getFontSize() - 2));
        }
    }

    private void toggleColumns(ActionEvent e) {
        TextReader reader = findComponent(TextReader.class);
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

}
