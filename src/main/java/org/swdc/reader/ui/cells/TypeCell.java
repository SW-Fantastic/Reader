package org.swdc.reader.ui.cells;

import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.views.TypeCellView;

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
        BorderPane pane = (BorderPane)view.getView();
        pane.setPrefWidth(getListView().getPrefWidth() - 28);
        if (empty) {
            setGraphic(null);
            return;
        }
        view.setType(item);
        setGraphic(view.getView());
    }
}
