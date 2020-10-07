package org.swdc.reader.ui.view.dialogs;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;

/**
 * Created by lenovo on 2019/5/26.
 */
@View(title = "lang@dialog-book-title",dialog = true)
public class BookEditDialog extends FXView {

    @Getter
    private Book book;

    public void setBook(Book book) {
        TextField txtTitle =this.findById("txtTitle");
        ComboBox<BookType> typeComboBox = this.findById("cbxType");
        if (book != null){
            txtTitle.setText(book.getTitle());
            typeComboBox.getSelectionModel().select(book.getType());
        } else {
            txtTitle.setText("");
            typeComboBox.getSelectionModel().select(null);
        }
        this.book = book;
    }


}
