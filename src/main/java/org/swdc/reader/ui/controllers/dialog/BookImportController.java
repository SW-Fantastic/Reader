package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.event.TypeRefreshEvent;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.views.dialog.BookImportView;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/5/28.
 */
@CommonsLog
@FXMLController
public class BookImportController implements Initializable{

    @Autowired
    private BookImportView view;

    @Autowired
    private BookService service;

    @FXML
    protected ComboBox<BookType> cbxType;

    private ObservableList<BookType> typeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbxType.setItems(typeList);
    }

    @PostConstruct
    protected void initData() {
        List<BookType> types = service.listTypes();
        typeList.clear();
        typeList.addAll(types);
    }

    @FXML
    protected void onCreate() {
        File bookFile = view.getBookFile();
        service.createBook(view.getBook(), bookFile);
        view.close();
    }

    @FXML
    protected void onCancel() {
        view.close();
    }

    @EventListener(TypeRefreshEvent.class)
    protected void refreshTypes () {
        List<BookType> types = service.listTypes();
        typeList.clear();
        typeList.addAll(types);
    }

}
