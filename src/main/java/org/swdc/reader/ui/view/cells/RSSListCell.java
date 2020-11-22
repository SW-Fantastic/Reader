package org.swdc.reader.ui.view.cells;

import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import org.swdc.reader.ui.controller.RSSData;

public class RSSListCell extends ListCell<RSSData> {

    private RSSCellView view;

    public RSSListCell(RSSCellView view) {
        this.view = view;
    }

    @Override
    protected void updateItem(RSSData item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            BorderPane pane = view.getView();
            pane.setPrefWidth(getListView().getWidth() - 32);
            view.setRssData(item);
            setGraphic(view.getView());
        }
    }
}
