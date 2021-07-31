package org.swdc.reader.ui.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.reader.entity.Book;

public class BookControlCell extends TableCell<Book,Void> {

    private FontawsomeService iconService;

    private EventEmitter events;

    public static class BookEditEvent extends AbstractEvent {

        public BookEditEvent(Object message) {
            super(message);
        }
    }


    public static class BookRemoveEvent extends AbstractEvent {

        public BookRemoveEvent(Object message) {
            super(message);
        }
    }

    public BookControlCell(FontawsomeService iconService, EventEmitter events) {
        this.iconService = iconService;
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
        Button btnEdit = new Button();
        this.setUpIcon("pencil",btnEdit);
        btnEdit.setOnAction((e) -> this.events.emit(new BookEditEvent(this.getTableRow().getItem())));

        Button btnRemove = new Button();
        this.setUpIcon("trash",btnRemove);
        btnRemove.setOnAction((e) -> this.events.emit(new BookRemoveEvent(this.getTableRow().getItem())));

        box.setAlignment(Pos.CENTER);
        box.setSpacing(8);
        box.getChildren().addAll(btnEdit,btnRemove);
        this.setGraphic(box);
    }

    private void setUpIcon(String icon, Button button) {
        button.setFont(iconService.getFont(FontSize.SMALL));
        button.setText(iconService.getFontIcon(icon));
        button.setPrefSize(16,16);
    }


}
