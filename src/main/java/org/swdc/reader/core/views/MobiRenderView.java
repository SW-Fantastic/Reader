package org.swdc.reader.core.views;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.reader.core.BookView;


/**
 * Created by lenovo on 2019/9/29.
 */
@View(stage = false)
public class MobiRenderView extends FXView implements BookView {

    private WebView view = null;

    @Getter
    private final String viewId = "mobiRenderView";


    @Override
    public Node getView() {
        return view;
    }

    @Override
    protected Parent create() {
        this.view = new WebView();
        this.view.setId(viewId);
        return view;
    }

    @Override
    public Node getToolsView() {
       return null;
    }
}
