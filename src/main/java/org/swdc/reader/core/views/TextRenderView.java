package org.swdc.reader.core.views;

import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.reader.core.BookView;


/**
 * Created by lenovo on 2019/5/31.
 */
@View(stage = false)
public class TextRenderView extends FXView implements BookView {

    private WebView view;

    @Getter
    private final String viewId = "webRenderView";

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
    public void initToolsView(HBox toolBox) {
        toolBox.getChildren().clear();
    }

}
