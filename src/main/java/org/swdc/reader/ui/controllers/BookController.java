package org.swdc.reader.ui.controllers;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.event.BooksRefreshEvent;
import org.swdc.reader.event.TypeRefreshEvent;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.BooksView;
import org.swdc.reader.ui.views.dialog.BookImportView;
import org.swdc.reader.ui.views.dialog.TypeAddDialog;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/5/22.
 */
@FXMLController
public class BookController implements Initializable{

    @Autowired
    private BookService service;

    @Autowired
    private BooksView view;

    @Autowired
    private TypeAddDialog addDialog;

    @Autowired
    private BookImportView importView;

    @FXML
    private ListView<BookType>  typeListView;

    @Autowired
    private ApplicationConfig config;

    private ObservableList<BookType> bookTypes = FXCollections.observableArrayList();

    private ObservableList<Book> books = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeListView.setItems(bookTypes);
        typeListView.getSelectionModel().selectedItemProperty().addListener(this::typeChange);
        List<BookType> types = service.listTypes();
        bookTypes.clear();
        bookTypes.addAll(types);
    }

    @PostConstruct
    protected void initData() {
        view.getBookGridView().setItems(books);
        books.clear();
        BookType defaultType = service.getDefaultType();
        books.addAll(service.getBooks(defaultType));
    }

    @FXML
    protected void onSyncFiles() throws Exception{
        service.syncBookFolder();
    }

    @FXML
    protected void onAddType() {
        addDialog.show();
    }

    @FXML
    protected void onOpen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("打开");
        File target = chooser.showOpenDialog(GUIState.getStage());
        if (target == null || !target.exists() || target.isDirectory()) {
            return;
        }
        importView.setBook(target);
        importView.show();
    }

    private void typeChange(ObservableValue<? extends BookType> typeObservableValue, BookType old, BookType newVal) {
        config.publishEvent(new BooksRefreshEvent());
    }

    @EventListener(BooksRefreshEvent.class)
    protected void onRefreshList() {
        Platform.runLater(() -> {
            BookType type = typeListView.getSelectionModel().getSelectedItem();
            books.clear();
            if (type == null) {
                BookType defaultType = service.getDefaultType();
                books.addAll(defaultType.getBooks());
            } else {
                books.addAll(service.getBooks(type));
                typeListView.getSelectionModel().select(type);
            }
        });
    }

    @EventListener(TypeRefreshEvent.class)
    public void onTypeRefresh() {
        Platform.runLater(() -> {
            List<BookType> types = service.listTypes();
            bookTypes.clear();
            bookTypes.addAll(types);
        });
    }

}
