package org.swdc.reader.ui.cells;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.reader.entity.Book;

public class MultipleImportEditCell extends TableCell<Book,Void> {

    private EventEmitter events;

    public static class BookImportEvent extends AbstractEvent {

        public BookImportEvent(Object message) {
            super(message);
        }
    }


    public MultipleImportEditCell(EventEmitter events) {
        this.events = events;
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        HBox box = new HBox();
        Button btnEdit = new Button("导入");
        btnEdit.setOnAction((e) -> this.events.emit(new BookImportEvent(this.getTableRow().getItem())));

        box.setAlignment(Pos.CENTER);
        box.setSpacing(8);
        box.getChildren().addAll(btnEdit);
        this.setGraphic(box);
    }

}
