package org.swdc.reader.ui.dialogs.mainview;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.FXResources;
import org.swdc.fx.StageCloseEvent;
import org.swdc.fx.view.Toast;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.events.TreeRefreshEvent;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.services.TypeServices;
import org.swdc.reader.ui.LanguageKeys;

import java.util.ResourceBundle;

public class EditTypeDialogController {

    private BookType type;

    @FXML
    private TextField txtName;

    @Inject
    private EditTypeDialog view;

    @Inject
    private TypeServices typeServices;

    @Inject
    private FXResources resources;


    @FXML
    public void onCancel() {
        view.hide();
    }

    @FXML
    public void onDelete() {
        ResourceBundle bundle = resources.getResourceBundle();
        Alert alert = this.view.alert(
                bundle.getString(LanguageKeys.KEY_BOOK_WARN),
                bundle.getString(LanguageKeys.KEY_DELETE_TYPE) +
                        " < " + type.getName() + " > " +
                        bundle.getString(LanguageKeys.KEY_QUESTION_SUBFIX),
                Alert.AlertType.CONFIRMATION
        );
        alert.showAndWait().ifPresent((buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                typeServices.remove(type.getId());
                Toast.showMessage(bundle.getString(LanguageKeys.KEY_TYPE_DELETED));
                this.view.emit(new TypeListRefreshEvent(""));
                this.view.emit(new TreeRefreshEvent(null));
                this.onCancel();
            }
        }));
    }

    @FXML
    public void onRename() {

        String newName = txtName.getText();
        if (newName == null || newName.isEmpty() || newName.equals(type.getName())) {
            this.onCancel();
            return;
        }
        typeServices.rename(type.getId(),newName);
        this.onCancel();

        ResourceBundle bundle = resources.getResourceBundle();

        Toast.showMessage(bundle.getString(LanguageKeys.KEY_TYPE_SAVED));
        this.view.emit(new TypeListRefreshEvent(""));
    }


    public void setType(BookType type) {
        this.type = type;
        this.txtName.setText(type.getName());
    }


    @EventListener(type = StageCloseEvent.class)
    public void onStageClose(StageCloseEvent event) {
        if (!event.getMessage().equals(this.view.getClass())) {
            return;
        }
        txtName.setText("");
        type = null;
    }
}
