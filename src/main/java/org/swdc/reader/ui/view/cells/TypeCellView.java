package org.swdc.reader.ui.view.cells;


import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.view.dialogs.TypeEditViewDialog;


/**
 *
 */
@Scope(ScopeType.MULTI)
@View(stage = false)
public class TypeCellView extends FXView {

    protected BookType type;

    @Aware
    private TypeEditViewDialog editViewDialog;

    @Aware
    private FontawsomeService fontawsomeService;

    @Override
    public void initialize() {
        Button typeName = this.findById("edit");
        typeName.setOnAction(this::onTypeEdit);
        typeName.setFont(fontawsomeService.getFont(FontSize.SMALL));
        typeName.setText(fontawsomeService.getFontIcon("pencil"));
    }

    public void setType(BookType type){
        this.type = type;
        Label typeName = this.findById("typeName");
        if (type != null) {
            typeName.setText(type.getName());
        } else {
            typeName.setText("");
        }
    }

    private void onTypeEdit(ActionEvent event) {
        if (type == null) {
            return;
        }
        editViewDialog.setType(type);
        editViewDialog.show();
    }

}
