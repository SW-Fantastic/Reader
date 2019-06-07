package org.swdc.reader.ui.controllers.dialog;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.views.dialog.MarkAddDialog;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/6/7.
 */
@FXMLController
public class MarkAddDialogController implements Initializable {

    @Autowired
    private MarkAddDialog view;

    @Autowired
    private BookService service;

    @FXML
    private TextField txtName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void onOk(){
        if (view.getBook() == null) {
            view.close();
            return;
        }
        BookReader reader = view.getBook();
        BookMark mark = new BookMark();
        mark.setLocation(reader.getLocator().getLocation());
        mark.setMarkFor(reader.getBook());
        mark.setDescription(txtName.getText());
        service.createBookMark(mark);
        txtName.setText("");
        view.close();
    }

    @FXML
    protected void onCancel() {
        txtName.setText("");
        view.close();
    }

}
