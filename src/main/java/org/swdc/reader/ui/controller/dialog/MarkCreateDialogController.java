package org.swdc.reader.ui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.services.BookService;
import org.swdc.reader.ui.view.dialogs.MarkCreateDialog;

public class MarkCreateDialogController extends FXController {

    @Aware
    private BookService service = null;

    @FXML
    private TextField txtName;

    @FXML
    protected void onOk(){
        MarkCreateDialog view = getView();
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
        MarkCreateDialog view = getView();
        txtName.setText("");
        view.close();
    }

}
