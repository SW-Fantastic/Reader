package org.swdc.reader.core.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.web.WebView;
import lombok.Getter;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/5/31.
 */
@FXMLView
public class WebRenderView extends AbstractFxmlView {

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
}
