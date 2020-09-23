package org.swdc.reader.ui.view.cells;

import javafx.scene.control.ListCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;

/**
 * Created by lenovo on 2019/5/23.
 */
public class TypeCell extends ListCell<BookType> {

    private TypeCellView view;

    public TypeCell(TypeCellView view) {
        this.view = view;
    }

    @Override
    protected void updateItem(BookType item, boolean empty) {
        super.updateItem(item, empty);
        this.setOnDragOver(event->{
            if (empty) {
                return;
            }
            if (event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });

        this.setOnDragDropped(event -> {
            if (empty) {
                return;
            }
            BookService service = view.findComponent(BookService.class);
            Dragboard dragboard = event.getDragboard();
            String itemId = dragboard.getString();
            Book book = service.getBook(Long.valueOf(itemId));
            book.setType(item);
            service.modifyBook(book);
        });

        BorderPane pane = view.getView();
        pane.setPrefWidth(getListView().getPrefWidth() - 28);
        if (empty) {
            setGraphic(null);
            return;
        }
        view.setType(item);
        setGraphic(view.getView());

    }
}
