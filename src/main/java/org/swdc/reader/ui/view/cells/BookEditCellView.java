package org.swdc.reader.ui.view.cells;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import lombok.Setter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.fx.resource.icons.FontSize;
import org.swdc.fx.resource.icons.FontawsomeService;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.events.ContentItemChangeEvent;
import org.swdc.reader.ui.events.DocumentOpenEvent;
import org.swdc.reader.ui.events.MarkItemChangeEvent;
import org.swdc.reader.ui.events.ViewChangeEvent;
import org.swdc.reader.ui.view.dialogs.BookEditDialog;
import org.swdc.reader.ui.view.dialogs.ContentsItemView;
import org.swdc.reader.ui.view.dialogs.MarksDialog;

@Scope(ScopeType.MULTI)
@View(stage = false)
public class BookEditCellView extends FXView {

    @Setter
    protected Book book;

    @Aware
    private BookEditDialog editDialog = null;

    @Aware
    private FontawsomeService fontawsomeService = null;

    @Aware
    private MarksDialog marksDialog = null;

    @Aware
    private ContentsItemView contentsItemView = null;

    @Override
    public void initialize() {
        setButtonIcon("btnOpen", "folder_open", this::onOpen);
        setButtonIcon("btnContents", "bars",  this::onContents);
        setButtonIcon("btnMark", "tag", this::onTag);
        setButtonIcon("btnEdit", "pencil", this::onBookEdit);
    }

    private void onBookEdit(ActionEvent event) {
        if (book == null) {
            return;
        }
        editDialog.setBook(book);
        editDialog.show();
    }

    private void onOpen(ActionEvent event) {
        if (book == null) {
            return;
        }
        this.emit(new ViewChangeEvent("read",this));
        this.emit(new DocumentOpenEvent(this.book,this));
        this.emit(new ContentItemChangeEvent(this.book,this));
    }

    private void onContents(ActionEvent event) {
        if (book == null) {
            return;
        }
        this.emit(new ContentItemChangeEvent(book,this));
        contentsItemView.show();
    }

    private void onTag(ActionEvent event) {
        if (book == null) {
            return;
        }
        this.emit(new MarkItemChangeEvent(book,this));
        marksDialog.show();
    }

    private void setButtonIcon(String id, String iconName, EventHandler<ActionEvent> handler) {
        Button btn = this.findById(id);
        btn.setFont(fontawsomeService.getFont(FontSize.SMALLEST));
        btn.setText(fontawsomeService.getFontIcon(iconName));
        btn.setOnAction(handler);
    }

}
