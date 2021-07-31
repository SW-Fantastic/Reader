package org.swdc.reader.ui.cells;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.swdc.dependency.EventEmitter;
import org.swdc.dependency.event.AbstractEvent;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.reader.entity.BookTag;

public class TagFlowCell {


    public static class TagRemoveEvent extends AbstractEvent {

        public TagRemoveEvent(BookTag message) {
            super(message);
        }
    }

    private HBox box;
    private BookTag tag;
    private FontawsomeService fontawsomeService;
    private EventEmitter eventEmitter;

    public TagFlowCell(BookTag book, FontawsomeService iconService, EventEmitter eventEmitter) {
        this.tag = book;
        this.fontawsomeService = iconService;
        this.eventEmitter = eventEmitter;
    }

    public HBox getView() {
        if (this.box != null) {
            return box;
        }
        box = new HBox();
        box.setAlignment(Pos.CENTER);

        Label name = new Label(this.tag.getName());
        Button remove = new Button();
        remove.setFont(fontawsomeService.getFont(FontSize.VERY_SMALL));
        remove.setText(fontawsomeService.getFontIcon("trash"));
        remove.setOnAction(e -> this.eventEmitter.emit(new TagRemoveEvent(tag)));

        box.getChildren().addAll(name,remove);
        box.getStyleClass().add("tag-cell");
        box.setPadding(new Insets(0,0,0,4));
        return box;
    }



}
