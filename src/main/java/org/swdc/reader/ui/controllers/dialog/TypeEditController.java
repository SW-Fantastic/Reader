package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.views.dialog.TypeEditViewDialog;
import org.swdc.reader.utils.UIUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/6/8.
 */
@FXMLController
public class TypeEditController implements Initializable {

    @Autowired
    private TypeEditViewDialog view;

    @Autowired
    private BookService service;

    @Autowired
    private ApplicationConfig config;

    @FXML
    private TextField txtName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void onCancel(){
        view.close();
    }

    @FXML
    protected void onExport(){
        if (view.getType() == null) {
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File target = directoryChooser.showDialog(GUIState.getStage());
        if (target == null) {
            return;
        }
        service.exportType(target, view.getType());
        UIUtils.showAlertDialog("所有的文档已经导出到了指定的文件夹。","提示", Alert.AlertType.INFORMATION, config);
    }

    @FXML
    protected void onMove() {

    }

    @FXML
    protected void nameModify() {
        if (view.getType() == null || txtName.getText().trim().equals("")){
            return;
        }
        BookType type = view.getType();
        type.setName(txtName.getText());
        service.modifyType(type);
    }

    @FXML
    protected void onDelete() {
        if (view.getType() == null) {
            return;
        }
        UIUtils.showAlertDialog(
                "确定要删除《"
                        + view.getType().getName() +
                        "》吗？这将会同时删除分类的文件以及相关记录。"
                ,"删除", Alert.AlertType.CONFIRMATION, config).ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                service.deleteType(view.getType());
                view.close();
            }
        });
    }

}
