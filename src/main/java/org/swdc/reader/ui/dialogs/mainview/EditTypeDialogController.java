package org.swdc.reader.ui.dialogs.mainview;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.StageCloseEvent;
import org.swdc.fx.view.Toast;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.events.TreeRefreshEvent;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.services.TypeServices;

public class EditTypeDialogController {

    private BookType type;

    @FXML
    private TextField txtName;

    @Inject
    private EditTypeDialog view;

    @Inject
    private TypeServices typeServices;


    @FXML
    public void onCancel() {
        view.hide();
    }

    @FXML
    public void onDelete() {
        Alert alert = this.view.alert("删除","的确要删除 " + type.getName() + " 吗？", Alert.AlertType.CONFIRMATION);
        alert.showAndWait().ifPresent((buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                typeServices.remove(type.getId());
                Toast.showMessage("分类已经删除。");
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

        Toast.showMessage("类型保存成功。");
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
