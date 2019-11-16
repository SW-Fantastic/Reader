package org.swdc.reader.core.views;

import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import lombok.Getter;
import org.swdc.reader.core.BookView;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/9/29.
 */
@FXMLView
public class MobiRenderView implements BookView {

    private BrowserView view;

    @Getter
    private final String viewId = "mobiRenderView";

    @PostConstruct
    public void initUI() {
        Platform.runLater(() ->{
            this.view = new BrowserView();
            this.view.setId(viewId);
        });
    }

    @Override
    public Node getView() {
        return view;
    }

    @Override
    public void initToolsView(HBox toolBox) {

    }
}
