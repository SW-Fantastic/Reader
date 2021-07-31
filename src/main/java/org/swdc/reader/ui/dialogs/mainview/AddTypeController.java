package org.swdc.reader.ui.dialogs.mainview;

import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.swdc.dependency.annotations.EventListener;
import org.swdc.fx.StageCloseEvent;
import org.swdc.reader.events.TypeListRefreshEvent;
import org.swdc.reader.services.TypeServices;

public class AddTypeController {

    @Inject
    private AddTypeDialog view;

    @Inject
    private TypeServices typeServices;

    @FXML
    private TextField txtName;

    @FXML
    public void cancelClick() {
        view.getStage().hide();
    }

    @FXML
    public void okClick() {
        String name = txtName.getText();
        if (name == null || name.isEmpty()) {
            return;
        }
        this.typeServices.create(name);
        this.view.emit(new TypeListRefreshEvent(""));
        this.cancelClick();
    }

    @EventListener(type = StageCloseEvent.class)
    public void onStageClose(StageCloseEvent event) {
        if (!event.getMessage().equals(this.view.getClass())) {
            return;
        }
        txtName.setText("");
    }


}
