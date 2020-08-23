package org.swdc.reader.ui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.view.dialogs.TypeAddDialog;

public class TypeAddDialogController extends FXController {

    @Aware
    private BookService bookService = null;

    @FXML
    protected TextField txtName;

    @FXML
    protected void onOk() {
        TypeAddDialog dialog = getView();
        if (txtName.getText().trim().equals("")) {
            dialog.showAlertDialog("提示","名字不能为空", Alert.AlertType.ERROR);
        } else if (bookService.isTypeExist(txtName.getText())) {
            dialog.showAlertDialog("提示","名字为《" + txtName.getText() + "》的类别已经存在", Alert.AlertType.ERROR);
        } else {
            bookService.createType(txtName.getText());
            dialog.close();
            txtName.setText("");
        }
    }

    @FXML
    protected void onCancel() {
        TypeAddDialog dialog = getView();
        dialog.close();
        txtName.setText("");
    }

}
