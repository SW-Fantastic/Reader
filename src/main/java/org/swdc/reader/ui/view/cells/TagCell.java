package org.swdc.reader.ui.view.cells;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import org.swdc.reader.entity.BookTag;

public class TagCell extends ListCell<BookTag> {

    private TagCellView view;

    public TagCell(TagCellView cellView) {
        this.view = cellView;
    }

    @Override
    protected void updateItem(BookTag item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            this.view.setTag(item);
            this.setGraphic(view.getView());
        }
    }

}
