package org.swdc.reader.ui.view.dialogs;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.View;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;

import java.io.File;

@View(dialog = true,title = "导入文档")
public class BookImportDialog extends FXView {

    @Getter
    private Book book = null;

    @Getter
    private File bookFile = null;

    @Aware
    private BookService service = null;

    public void setBook(File bookFile) {
        ComboBox<BookType> typeComboBox = this.findById("cbxType");
        TextField txtTitle = this.findById("txtTitle");
        if (bookFile == null) {
            typeComboBox.getSelectionModel().select(null);
            txtTitle.setText("");
            return;
        }
        this.bookFile = bookFile;
        Book book = service.fromFile(bookFile);
        typeComboBox.getSelectionModel().select(book.getType());
        txtTitle.setText(book.getTitle());
        this.book = book;
    }

    @Override
    public void close() {
        super.close();
        setBook(null);
    }
}
