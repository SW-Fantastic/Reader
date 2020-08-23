package org.swdc.reader.ui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.view.MainView;
import org.swdc.reader.ui.view.dialogs.TypeEditViewDialog;

import java.io.File;

public class TypeEditController extends FXController {

    @Aware
    private BookService service = null;

    @FXML
    private TextField txtName;

    @FXML
    public void onDelete() {
        TypeEditViewDialog view = getView();
        if (view.getType() == null) {
            return;
        }
        view.showAlertDialog("删除","确定要删除《"
                + view.getType().getName() +
                "》吗？这将会同时删除分类的文件以及相关记录。", Alert.AlertType.CONFIRMATION).ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.deleteType(view.getType());
                view.close();
            }
        });
    }

    @FXML
    public void onMove() {

    }

    @FXML
    public void onExport() {
        TypeEditViewDialog view = getView();
        if (view.getType() == null) {
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File target = directoryChooser.showDialog(findView(MainView.class).getStage());
        if (target == null) {
            return;
        }
        service.exportType(target, view.getType());
        view.showAlertDialog("提示","所有的文档已经导出到了指定的文件夹。", Alert.AlertType.INFORMATION);
    }

    @FXML
    public void onCancel() {
        TypeEditViewDialog view = getView();
        view.close();
    }

    @FXML
    public void nameModify() {
        TypeEditViewDialog view = getView();
        if (view.getType() == null || txtName.getText().trim().equals("")){
            return;
        }
        BookType type = view.getType();
        type.setName(txtName.getText());
        service.modifyType(type);
    }

}
