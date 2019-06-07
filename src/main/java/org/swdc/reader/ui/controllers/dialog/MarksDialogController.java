package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.event.*;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.dialog.MarksDialog;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/6/7.
 */
@FXMLController
public class MarksDialogController implements Initializable {

    @Autowired
    private BookService service;

    @Autowired
    private ApplicationConfig config;

    @FXML
    private ListView<BookMark> itemList;

    @Autowired
    private MarksDialog view;

    private ObservableList<BookMark> marks = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        itemList.setItems(marks);
    }

    @EventListener(MarkItemChangeEvent.class)
    public void onMarkChange(MarkItemChangeEvent event) {
        Platform.runLater(() -> {
            Book book = service.getBook(event.getSource().getId());
            marks.clear();
            marks.addAll(book.getMarks());
        });
    }

    @FXML
    protected void onOK() {
        BookMark mark = itemList.getSelectionModel().getSelectedItem();
        if (mark == null) {
            return;
        }
        config.publishEvent(new DocumentOpenEvent(mark.getMarkFor()));
        config.publishEvent(new BookLocationEvent(mark.getLocation()));
        config.publishEvent(new ViewChangeEvent("read"));
        view.close();
    }

    @FXML
    protected void onCancel() {
        view.close();
    }

    @FXML
    protected void onDelete() {
        BookMark mark = itemList.getSelectionModel().getSelectedItem();
        if (mark == null) {
            return;
        }
        service.deleteMark(mark);
        marks.remove(mark);
    }

}
