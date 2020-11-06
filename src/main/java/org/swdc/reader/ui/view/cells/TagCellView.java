package org.swdc.reader.ui.view.cells;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.entity.BookTag;

@View
@Scope(ScopeType.MULTI)
public class TagCellView extends FXView {

    public interface Handler {
        void invoke(TagCellView cellView, ActionEvent e);
    }

    private Label labelName;

    private HBox container;

    private BookTag tag;

    private Button btnRemove;

    @Override
    protected Parent create() {
        if (container == null) {
            container = new HBox();
            labelName = new Label();

            FontawsomeService fontawsomeService = this.findComponent(FontawsomeService.class);

            btnRemove = new Button();
            btnRemove.setFont(fontawsomeService.getFont(FontSize.VERY_SMALL));
            btnRemove.setText(fontawsomeService.getFontIcon("trash"));
            btnRemove.setPadding(new Insets(4,4,4,4));
            HBox.setMargin(btnRemove,new Insets(0,0,0,4));

            HBox align = new HBox();
            HBox.setHgrow(align,Priority.ALWAYS);

            container.getChildren().add(labelName);
            container.getChildren().add(align);
            container.getChildren().add(btnRemove);
            container.setAlignment(Pos.CENTER);
            container.setPadding(new Insets(0,6,0,6));

            HBox.setHgrow(container, Priority.ALWAYS);
            container.getStyleClass().add("tag-cell");
        }
        return container;
    }

    public void setTag(BookTag tag) {
        this.tag = tag;
        this.labelName.setText(tag.getName());
    }

    public BookTag getTag() {
        return tag;
    }

    public TagCellView withAction(Handler onClick) {
        this.btnRemove.setOnAction(e -> onClick.invoke(this,e));
        return this;
    }

}
