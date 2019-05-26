package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.dialog.TypeAddDialog;
import org.swdc.reader.utils.UIUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/5/25.
 */
@FXMLController
public class TypeAddDialogController implements Initializable {

    @Autowired
    private BookService bookService;

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private TypeAddDialog view;

    @FXML
    protected TextField txtName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void onOk() {
        if (txtName.getText().trim().equals("")) {
            UIUtils.showAlertDialog("名字不能为空", "提示", Alert.AlertType.ERROR, config);
        } else if (bookService.isTypeExist(txtName.getText())) {
            UIUtils.showAlertDialog("名字为《" + txtName.getText() + "》的类别已经存在", "提示", Alert.AlertType.ERROR, config);
        } else {
            bookService.createType(txtName.getText());
            view.close();
            txtName.setText("");
        }
    }

    @FXML
    protected void onCancel() {
        view.close();
        txtName.setText("");
    }

}
