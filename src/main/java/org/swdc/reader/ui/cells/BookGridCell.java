package org.swdc.reader.ui.cells;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.GridCell;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.views.BookCellView;

/**
 * Created by lenovo on 2019/5/22.
 */
public class BookGridCell extends GridCell<Book> {

    private BookCellView view;

    public BookGridCell(BookCellView view) {
        this.view = view;
    }

    @Override
    protected void updateItem(Book item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        view.setBook(item);
        setGraphic(view.getView());
    }
}
