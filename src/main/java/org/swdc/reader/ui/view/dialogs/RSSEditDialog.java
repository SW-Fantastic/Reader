package org.swdc.reader.ui.view.dialogs;

import javafx.scene.control.TextField;
import org.swdc.fx.anno.View;
import org.swdc.reader.entity.RSSSource;

@View(title = "lang@feed-edit",dialog = true)
public class RSSEditDialog extends RSSAddDialog {

    private RSSSource source;

    public void setSource(RSSSource source) {
        TextField txtName = findById("name");
        TextField txtAddress = findById("address");
        if (source == null) {
            txtAddress.setText(null);
            txtName.setText(null);
            return;
        }
        txtAddress.setText(source.getUrl());
        txtName.setText(source.getName());
        this.source = source;
    }

    public RSSSource getSource() {
        return source;
    }
}
