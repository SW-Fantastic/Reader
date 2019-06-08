package org.swdc.reader.ui.views.dialog;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.anno.UIMethod;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Created by lenovo on 2019/5/28.
 */
@FXMLView("/views/dialogs/BookImport.fxml")
public class BookImportView extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private BookService service;

    private Stage stage;

    @Getter
    private Book book;

    @Getter
    private File bookFile;

    @PostConstruct
    protected void initUI () throws Exception {
        UIUtils.configUI((BorderPane)this.getView(), config);
        Platform.runLater(() -> {
            stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(this.getView());
            stage.setScene(scene);
            stage.getIcons().addAll(AwsomeIconData.getImageIcons());
            stage.setTitle("书籍");
            stage.setResizable(false);
            stage.initOwner(GUIState.getStage());
        });
    }

    @UIMethod
    public void show() {
        if (stage.isShowing()) {
                stage.requestFocus();
        } else {
                stage.showAndWait();
        }
    }

    public void setBook(File bookFile) {
        BorderPane pane = (BorderPane) this.getView();
        ComboBox<BookType> typeComboBox = (ComboBox) pane.getCenter().lookup("#cbxType");
        TextField txtTitle = (TextField)pane.getCenter().lookup("#txtTitle");
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

    @UIMethod
    public void close() {
        if (stage.isShowing()) {
            stage.close();
            setBook(null);
        }
    }

}
