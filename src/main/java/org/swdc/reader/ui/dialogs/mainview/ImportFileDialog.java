package org.swdc.reader.ui.dialogs.mainview;


import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

import java.io.File;

@View(viewLocation = "/views/dialogs/ImportFile.fxml",
        title = "导入文件",dialog = true,resizeable = false)
public class ImportFileDialog extends AbstractView {

    @Inject
    private FontawsomeService icon;

    @PostConstruct
    public void initialize() {
        Button button = this.findById("addType");
        button.getStyleClass().add("btn-ghost");
        this.setIcon(button,"plus");
    }

    private void setIcon(Button btn, String name) {
        btn.setFont(icon.getFont(FontSize.VERY_SMALL));
        btn.setText(icon.getFontIcon(name));
        btn.setPadding(new Insets(6,6,6,6));
    }

    public void showWithFile(File file) {
        ImportFileDialogController controller = this.getController();
        controller.withFile(file);
        this.show();
    }

}
