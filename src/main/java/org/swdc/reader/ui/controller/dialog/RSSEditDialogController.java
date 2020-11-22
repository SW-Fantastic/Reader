package org.swdc.reader.ui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.entity.RSSSource;
import org.swdc.reader.services.RSSService;
import org.swdc.reader.ui.view.dialogs.RSSEditDialog;

public class RSSEditDialogController extends FXController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @Aware
    private RSSService service = null;

    @FXML
    public void onSave() {
        String address = txtAddress.getText();
        if (address == null || address.isBlank()) {
            return;
        }
        String name = txtName.getText();
        if (name == null || name.isBlank()) {
            return;
        }

        RSSEditDialog editDialog = getView();

        RSSSource source = editDialog.getSource();
        if (source == null) {
            return;
        }
        source.setName(name);
        source.setUrl(address);
        service.modifyRssSource(source);

        editDialog.close();
    }

    @FXML
    public void onCancel() {
        RSSEditDialog dialog = getView();
        dialog.reset();
        dialog.setSource(null);
        dialog.close();
    }

}
