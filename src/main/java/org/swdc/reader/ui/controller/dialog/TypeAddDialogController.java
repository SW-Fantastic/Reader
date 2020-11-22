package org.swdc.reader.ui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.entity.BookType;
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

            dialog.showAlertDialog(i18n("lang@dialog-warn"),
                    i18n("lang@dialog-warn-name"),
                    Alert.AlertType.ERROR);

        } else if (bookService.isTypeExist(txtName.getText())) {

            dialog.showAlertDialog(i18n("lang@dialog-warn"),
                    i18n("lang@type-conflict") + txtName.getText(),
                    Alert.AlertType.ERROR);

        } else {
            BookType type = new BookType();
            type.setName(txtName.getText());
            bookService.createType(type);
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
