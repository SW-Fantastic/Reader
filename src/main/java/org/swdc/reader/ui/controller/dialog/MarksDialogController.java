package org.swdc.reader.ui.controller.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.core.event.BookLocationEvent;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.events.DocumentOpenEvent;
import org.swdc.reader.ui.events.MarkItemChangeEvent;
import org.swdc.reader.ui.events.ViewChangeEvent;
import org.swdc.reader.ui.view.dialogs.MarksDialog;

import java.net.URL;
import java.util.ResourceBundle;

public class MarksDialogController extends FXController {

    @FXML
    private ListView<BookMark> itemList;

    @Aware
    private BookService service = null;

    private ObservableList<BookMark> marks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemList.setItems(marks);
    }

    @Listener(value = MarkItemChangeEvent.class,updateUI = true)
    public void onMarkChange(MarkItemChangeEvent event) {
        Book book = service.getBook(event.getData().getId());
        marks.clear();
        marks.addAll(book.getMarks());
    }

    @FXML
    public void onOK() {
        MarksDialog view = this.getView();
        BookMark mark = itemList.getSelectionModel().getSelectedItem();
        if (mark == null) {
            return;
        }
        this.emit(new DocumentOpenEvent(mark.getMarkFor(),this));
        this.emit(new BookLocationEvent(mark.getLocation(),this));
        this.emit(new ViewChangeEvent("read",this));
        view.close();
    }

    @FXML
    public void onCancel() {
        MarksDialog view = this.getView();
        view.close();
    }

    @FXML
    public void onDelete() {
        BookMark mark = itemList.getSelectionModel().getSelectedItem();
        if (mark == null) {
            return;
        }
        service.deleteMark(mark);
        marks.remove(mark);
    }

}
