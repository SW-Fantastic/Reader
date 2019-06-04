package org.swdc.reader.core.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.swdc.reader.core.BookView;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/5/31.
 */
@FXMLView
public class TextRenderView extends AbstractFxmlView implements BookView {

    private WebView view;

    @Getter
    private final String viewId = "webRenderView";

    @PostConstruct
    protected void initUI() {
        Platform.runLater(() ->{
            this.view = new WebView();
            this.view.setId(viewId);
        });
    }

    @Override
    public Parent getView() {
        return this.view;
    }

    @Override
    public void initToolsView(HBox toolBox) {
        toolBox.getChildren().clear();
    }

}
