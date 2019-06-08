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
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/5/26.
 */
@FXMLView("/views/dialogs/BookEdit.fxml")
public class BookEditDialog extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    private Stage stage;

    @Getter
    private Book book;

    @PostConstruct
    protected void initUI () throws Exception {
        UIUtils.configUI((BorderPane)this.getView(), config);
        Platform.runLater(() -> {
            stage = new Stage();
            Scene scene = new Scene(this.getView());
            stage.setScene(scene);
            stage.setTitle("书籍");
            stage.getIcons().addAll(AwsomeIconData.getImageIcons());
            stage.setResizable(false);
            stage.initOwner(GUIState.getStage());
            stage.initModality(Modality.APPLICATION_MODAL);
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

    public void setBook(Book book) {
        BorderPane pane = (BorderPane) this.getView();
        TextField txtTitle = (TextField)pane.getCenter().lookup("#txtTitle");
        ComboBox<BookType> typeComboBox = (ComboBox) pane.getCenter().lookup("#cbxType");
        if (book != null){
            txtTitle.setText(book.getTitle());
            typeComboBox.getSelectionModel().select(book.getType());
        } else {
            txtTitle.setText("");
            typeComboBox.getSelectionModel().select(null);
        }
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
