package org.swdc.reader.core.views;

import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.reader.core.BookView;


/**
 * Created by lenovo on 2019/6/9.
 */
@View(stage = false)
public class PDFRenderView extends FXView implements BookView {

    private Canvas canvas;
    private ScrollPane pane;

    @Getter
    private final String viewId = "pdfRenderView";

    @Override
    public Parent getView() {
        return pane;
    }

    @Override
    protected Parent create() {
        canvas = new Canvas();
        pane = new ScrollPane(canvas);
        canvas.widthProperty().bind(pane.widthProperty());
        pane.setId(viewId);
        return pane;
    }

    @Override
    public void initToolsView(HBox toolBox) {
        toolBox.getChildren().clear();
    }
}
