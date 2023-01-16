package org.swdc.reader.ui.dialogs.mainview;

import jakarta.annotation.PostConstruct;
import javafx.scene.control.TextField;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

@View(viewLocation = "/views/dialogs/AddType.fxml",
        title = "创建分类",dialog = true,resizeable = false)
public class AddTypeDialog  extends AbstractView {

    @PostConstruct
    public void onInit() {
        getStage().setOnHiding(e -> {
            TextField field = findById("txtName");
            field.setText("");
        });
    }

}
