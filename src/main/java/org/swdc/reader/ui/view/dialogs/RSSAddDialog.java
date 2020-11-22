package org.swdc.reader.ui.view.dialogs;

import javafx.scene.control.TextField;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;

@View(title = "lang@feed-add",dialog = true)
public class RSSAddDialog extends FXView {

    public void reset() {
        TextField txtName = findById("name");
        TextField txtAddress = findById("address");
        txtAddress.setText("");
        txtName.setText("");
    }

}
