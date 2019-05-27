package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.event.TypeRefreshEvent;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.dialog.BookEditDialog;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/5/26.
 */
@FXMLController
public class BookEditDialogController implements Initializable {

    @Autowired
    private BookService service;

    @Autowired
    private BookEditDialog view;

    @Autowired
    private ApplicationConfig config;

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

    @EventListener(TypeRefreshEvent.class)
    protected void refreshTypes () {
        List<BookType> types = service.listTypes();
        typeList.clear();
        typeList.addAll(types);
    }

    @FXML
    protected void onModify() {
        Book book = view.getBook();
        book.setType(cbxType.getSelectionModel().getSelectedItem());
        service.modifyBook(book);
        view.close();
    }

    @FXML
    protected void onDelete() {
        Book book = view.getBook();
        Optional<ButtonType> selected = UIUtils.showAlertDialog("确定要删除《" + book.getTitle() + "》吗？",
                "提示", Alert.AlertType.CONFIRMATION, config);
        selected.ifPresent(type -> {
            if (type.equals(ButtonType.OK)) {
                service.deleteBook(book);
                view.close();
            }
        });
    }

    @FXML
    protected void onCancel() {
        view.close();
    }

}
