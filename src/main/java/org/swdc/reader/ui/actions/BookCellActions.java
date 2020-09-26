package org.swdc.reader.ui.actions;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.swdc.fx.services.Service;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.events.ContentItemChangeEvent;
import org.swdc.reader.ui.events.DocumentOpenEvent;
import org.swdc.reader.ui.events.MarkItemChangeEvent;
import org.swdc.reader.ui.events.ViewChangeEvent;
import org.swdc.reader.ui.view.dialogs.BookEditDialog;
import org.swdc.reader.ui.view.dialogs.ContentsItemView;
import org.swdc.reader.ui.view.dialogs.MarksDialog;

/**
 * 这个是用在BookCell上面的零散的事件处理方法，
 * 他们还可能会用在ContextMenu等多个位置，因此
 * 集中在一起会比较好。
 */
public class BookCellActions extends Service {

    public EventHandler<ActionEvent> openBook(ObservableValue<Book> book) {
        return e-> {
            if (book == null || book.getValue() == null) {
                return;
            }
            this.emit(new ViewChangeEvent("read",this));
            this.emit(new DocumentOpenEvent(book.getValue(),this));
            this.emit(new ContentItemChangeEvent(book.getValue(),this));
        };
    }

    public EventHandler<ActionEvent> openContentDialog(ObservableValue<Book> book) {
        return e-> {
            if (book == null || book.getValue() == null) {
                return;
            }
            this.emit(new ContentItemChangeEvent(book.getValue(),this));
            findView(ContentsItemView.class).show();
        };
    }

    public EventHandler<ActionEvent> openBookMarksDialog(ObservableValue<Book> book) {
        return e-> {
            if (book == null || book.getValue() == null) {
                return;
            }
            this.emit(new MarkItemChangeEvent(book.getValue(),this));
            findView(MarksDialog.class).show();
        };
    }

    public EventHandler<ActionEvent> openEditDialog(ObservableValue<Book> book) {
        return e -> {
            if (book == null || book.getValue() == null) {
                return;
            }
            BookEditDialog editDialog = findView(BookEditDialog.class);
            editDialog.setBook(book.getValue());
            editDialog.show();
        };
    }

}
