package org.swdc.reader.ui.controller.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.fx.anno.Listener;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.services.CommonComponents;
import org.swdc.reader.ui.events.TypeRefreshEvent;
import org.swdc.reader.ui.view.ReadView;
import org.swdc.reader.ui.view.dialogs.BookEditDialog;

import java.net.URL;
import java.util.*;

public class BookEditDialogController extends FXController  {

    @FXML
    protected ComboBox<BookType> cbxType;

    @FXML
    protected TextField txtTag;

    @FXML
    protected TextField txtAuthor;

    @FXML
    protected TextField txtPublisher;

    @Aware
    private BookService service = null;

    @Aware
    private CommonComponents commonComponents = null;

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

    @Listener(value = TypeRefreshEvent.class,updateUI = true)
    public void refreshTypes (TypeRefreshEvent refreshEvent) {
        List<BookType> types = service.listTypes();
        typeList.clear();
        typeList.addAll(types);
    }

    @FXML
    public void onCancel() {
        BookEditDialog view = getView();
        view.close();
    }

    @FXML
    public void onModify() {
        BookEditDialog view = getView();

        Book book = view.getBook();
        book.setType(cbxType.getSelectionModel().getSelectedItem());
        book.setAuthor(txtAuthor.getText());
        book.setPublisher(txtPublisher.getText());
        book.setTags(new HashSet<>(view.getTags()));

        commonComponents.submitTask(() -> service.modifyBook(book));

        view.close();
    }

    @FXML
    public void saveTag() {
        BookEditDialog view = getView();
        if (txtTag.getText().isBlank() || txtTag.getText().isEmpty()) {
            view.getPopOver().hide();
            return;
        }
        String name = txtTag.getText();
        BookTag tag = service.getTag(name);
        if (tag != null) {
            view.getTags().add(tag);
            view.getPopOver().hide();
            txtTag.clear();
            return;
        }
        tag = new BookTag();
        tag.setName(name);
        tag = service.saveTag(tag);
        view.getTags().add(tag);
        view.getPopOver().hide();
        txtTag.clear();
    }

    @FXML
    public void onDelete() {
        BookEditDialog view = getView();
        Book book = view.getBook();
        Optional<ButtonType> selected = view.showAlertDialog("提示","确定要删除《" + book.getTitle() + "》吗？", Alert.AlertType.CONFIRMATION);
        selected.ifPresent(type -> {
            if (type.equals(ButtonType.OK)) {
                ReadView readView = findView(ReadView.class);
                Book opened = readView.getOpenedBook();
                if (opened != null && opened.getId().equals(book.getId())) {
                    readView.closeBook();
                }
                service.deleteBook(book);
                view.close();
            }
        });
    }

}
