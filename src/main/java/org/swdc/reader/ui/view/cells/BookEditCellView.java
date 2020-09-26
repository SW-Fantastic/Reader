package org.swdc.reader.ui.view.cells;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.actions.BookCellActions;
import org.swdc.reader.ui.view.dialogs.BookEditDialog;
import org.swdc.reader.ui.view.dialogs.ContentsItemView;
import org.swdc.reader.ui.view.dialogs.MarksDialog;

@Scope(ScopeType.MULTI)
@View(stage = false)
public class BookEditCellView extends FXView {

    protected final SimpleObjectProperty<Book> book = new SimpleObjectProperty<>();

    @Aware
    private FontawsomeService fontawsomeService = null;

    @Override
    public void initialize() {
        BookCellActions actions = findComponent(BookCellActions.class);
        setButtonIcon("btnOpen", "folder_open", actions.openBook(book));
        setButtonIcon("btnContents", "bars",  actions.openContentDialog(book));
        setButtonIcon("btnMark", "tag", actions.openBookMarksDialog(book));
        setButtonIcon("btnEdit", "pencil", actions.openEditDialog(book));
    }

    public void setBook(Book book) {
        this.book.set(book);
    }

    private void setButtonIcon(String id, String iconName, EventHandler<ActionEvent> handler) {
        Button btn = this.findById(id);
        btn.setFont(fontawsomeService.getFont(FontSize.SMALLEST));
        btn.setText(fontawsomeService.getFontIcon(iconName));
        btn.setOnAction(handler);
    }

}
