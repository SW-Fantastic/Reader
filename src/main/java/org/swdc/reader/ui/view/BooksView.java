package org.swdc.reader.ui.view;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.view.cells.TypeCell;
import org.swdc.reader.ui.view.cells.TypeCellView;

@View(stage = false)
public class BooksView extends FXView {

    @Aware
    private FontawsomeService fontawsomeService = null;

    @Override
    public void initialize() {
        this.setButtonIcon("btnSearch", "search");
        this.setButtonIcon("btnOpen", "folder_open");
        this.setButtonIcon("btnRefresh", "refresh");
        this.setButtonIcon("btnType", "plus");

        ListView<BookType> typeList = this.findById("typelist");
        typeList.setCellFactory(list->new TypeCell(this.findView(TypeCellView.class)));
    }

    private void setButtonIcon(String btnId, String iconName) {
        Button btn = this.findById(btnId);
        if (btn == null) {
            return;
        }
        btn.setFont(fontawsomeService.getFont(FontSize.MIDDLE_SMALL));
        btn.setText(fontawsomeService.getFontIcon(iconName));
    }

}
