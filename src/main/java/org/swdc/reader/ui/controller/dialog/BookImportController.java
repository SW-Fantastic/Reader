package org.swdc.reader.ui.controller.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.events.TypeRefreshEvent;
import org.swdc.reader.ui.view.dialogs.BookImportDialog;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BookImportController extends FXController {

    @Aware
    private BookService service = null;

    @FXML
    protected ComboBox<BookType> cbxType;

    private ObservableList<BookType> typeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbxType.setItems(typeList);
    }

    @Override
    public void initialize() {
        List<BookType> types = service.listTypes();
        typeList.clear();
        typeList.addAll(types);
    }

    @FXML
    protected void onCreate() {
        BookImportDialog view = getView();
        File bookFile = view.getBookFile();
        service.createBook(view.getBook(), bookFile);
        view.close();
    }

    @FXML
    protected void onCancel() {
        BookImportDialog view = getView();
        view.close();
    }

    @Listener(value = TypeRefreshEvent.class,updateUI = true)
    public void refreshTypes (TypeRefreshEvent event) {
        List<BookType> types = service.listTypes();
        typeList.clear();
        typeList.addAll(types);
    }

}
