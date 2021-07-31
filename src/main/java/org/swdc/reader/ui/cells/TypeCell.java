package org.swdc.reader.ui.cells;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.dialogs.mainview.EditTypeDialog;

public class TypeCell extends ListCell<BookType> {

    private FontawsomeService fontawsomeService;

    private EditTypeDialog editDialog;

    public TypeCell(FontawsomeService iconServ, EditTypeDialog editDialog) {
        this.fontawsomeService = iconServ;
        this.editDialog = editDialog;
    }

    @Override
    protected void updateItem(BookType item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            this.setGraphic(null);
            return;
        }

        HBox layout = new HBox();

        HBox left = new HBox();
        left.setFillHeight(true);
        left.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(left, Priority.ALWAYS);
        layout.getChildren().add(left);

        HBox right = new HBox();
        right.setFillHeight(true);
        right.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(right, Priority.ALWAYS);
        layout.getChildren().add(right);

        Label label = new Label();
        label.setText(item.getName());
        left.getChildren().add(label);

        Button edit = new Button();
        edit.setFont(fontawsomeService.getFont(FontSize.VERY_SMALL));
        edit.setText(fontawsomeService.getFontIcon("pencil"));
        edit.setOnAction((e) -> editDialog.showWithType(item));
        right.getChildren().add(edit);

        layout.getStyleClass().add("type-cell");
        this.setGraphic(layout);
    }

}
