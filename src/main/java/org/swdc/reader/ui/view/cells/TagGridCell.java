package org.swdc.reader.ui.view.cells;

import org.controlsfx.control.GridCell;
import org.swdc.reader.entity.BookTag;

public class TagGridCell extends GridCell<BookTag> {

    private TagCellView cellView;

    public TagGridCell(TagCellView cellView) {
        this.cellView = cellView;
    }

    @Override
    protected void updateItem(BookTag item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            this.cellView.setTag(item);
            this.setGraphic(cellView.getView());
        }
    }
}
