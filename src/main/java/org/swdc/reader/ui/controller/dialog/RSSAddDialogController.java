package org.swdc.reader.ui.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.fx.FXController;
import org.swdc.fx.anno.Aware;
import org.swdc.reader.entity.RSSSource;
import org.swdc.reader.services.RSSService;
import org.swdc.reader.ui.view.dialogs.RSSAddDialog;

public class RSSAddDialogController extends FXController {

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

        RSSSource source = service.findByAddress(address);
        if (source != null) {
            return;
        }

        source = new RSSSource();
        source.setName(name);
        source.setUrl(address);
        service.createRssSource(source);

        RSSAddDialog dialog = getView();
        dialog.close();
    }

    @FXML
    public void onCancel() {
        RSSAddDialog dialog = getView();
        dialog.reset();
        dialog.close();
    }

}
