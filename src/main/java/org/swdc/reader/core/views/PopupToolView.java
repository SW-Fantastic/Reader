package org.swdc.reader.core.views;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class PopupToolView extends Popup {

    private BorderPane root;

    private Stage parent;

    public PopupToolView(Stage parent) {
        root = new BorderPane();
        this.parent = parent;
        this.getContent().add(root);

        this.setAnchorX(parent.getX() + (parent.getWidth() / 2) - (this.getWidth() / 2));
        this.setAnchorY(parent.getY() + 92);

        parent.xProperty().addListener(x-> {
            this.setAnchorX(parent.getX() + (parent.getWidth() / 2) - (this.getWidth() / 2));
        });

        parent.yProperty().addListener(y -> {
            this.setAnchorY(parent.getY() + 92);
        });

        this.setAutoHide(true);
        this.setHideOnEscape(true);
    }

    public void setGraph(Node node) {
        if (!node.getStyleClass().contains("float-tools")) {
            node.getStyleClass().add("float-tools");
        }
        this.root.setCenter(node);
        this.root.requestLayout();
        this.root.autosize();
    }

    public void showPopup() {
        this.setAnchorX(parent.getX() + (parent.getWidth() / 2) - (this.getWidth() / 2));
        this.setAnchorY(parent.getY() + 92);
        this.show(parent);
    }

}
