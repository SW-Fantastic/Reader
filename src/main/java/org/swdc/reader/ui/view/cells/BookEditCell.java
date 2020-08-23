package org.swdc.reader.ui.view.cells;

import javafx.scene.control.TableCell;
import org.swdc.reader.entity.Book;

public class BookEditCell extends TableCell<Book, Void> {

    private BookEditCellView cellView;

    public BookEditCell(BookEditCellView view) {
        this.cellView = view;
    }

    @Override
    protected void updateItem(Void aVoid, boolean empty) {
        super.updateItem(aVoid,empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        cellView.setBook(getTableView().getItems().get(getIndex()));
        setGraphic(cellView.getView());
    }
}
