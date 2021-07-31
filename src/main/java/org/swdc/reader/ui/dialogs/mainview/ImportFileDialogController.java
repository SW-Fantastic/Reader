package org.swdc.reader.ui.dialogs.mainview;

import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.StageCloseEvent;
import org.swdc.reader.core.BookDescriptor;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.services.BookServices;
import org.swdc.reader.services.TypeServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class ImportFileDialogController {

    @FXML
    private TextField fileName;

    @FXML
    private ComboBox<BookType> typeComb;

    @Inject
    private TypeServices typeServices;

    @Inject
    private ImportFileDialog dialog;

    @Inject
    private FXResources resources;

    @Inject
    private BookEditDialog bookEditDialog;

    @Inject
    private BookServices bookServices;

    @Inject
    private AddTypeDialog addTypeDialog;

    private File file;

    private ObservableList<BookType> types = FXCollections.observableArrayList();

    public void withFile(File file) {
        this.file = file;
        this.fileName.setText(file.getName());
    }

    @FXML
    public void onCancel() {
        this.dialog.hide();
    }

    @FXML
    public void onSave() {
        try {

            BookType type = typeComb.getSelectionModel().getSelectedItem();
            if (type == null) {
                Alert errModal = this.dialog.alert("错误","请指定文档的分类！", Alert.AlertType.ERROR);
                errModal.show();
                return;
            }

            File file = resources.getAssetsFolder();
            Path bookFolder = file.toPath().resolve("library");
            if (!Files.exists(bookFolder)) {
                bookFolder = Files.createDirectories(bookFolder);
            }
            FileInputStream fin = new FileInputStream(this.file);
            OutputStream fout = Files.newOutputStream(bookFolder.resolve(this.file.getName()));
            fin.transferTo(fout);
            fin.close();
            fout.close();

            Book book = new Book();
            book.setName(this.file.getName());
            book.setTitle(this.file.getName());
            book.setCreateDate(new Date());
            book.setType(type);

            book = bookServices.createBook(book);
            bookEditDialog.showWithBook(book);
            this.onCancel();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void addType() {
        this.addTypeDialog.show();
    }

    /**
     * 监听TypeList的变化事件。
     * Type出现刷新或者改变的时候将会触发。
     * @param event
     */
    @EventListener(type = TypeListRefreshEvent.class)
    public void refreshTypeLists(TypeListRefreshEvent event) {
        types.clear();
        List<BookType> typesList = typeServices.allTypes();
        this.types.clear();
        this.types.addAll(typesList);

        typeComb.setItems(this.types);

    }

    /**
     * Stage关闭的时候将会收到此事件。
     * 如果是本Stage的关闭，则执行一些数据的清理。
     * @param event
     */
    @EventListener(type = StageCloseEvent.class)
    public void onStageClose(StageCloseEvent event) {
        if (!event.getMessage().equals(this.dialog.getClass())) {
            return;
        }
        fileName.setText("");
        file = null;
    }

}
