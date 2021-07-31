package org.swdc.reader.ui.dialogs.mainview;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;

import java.io.File;

@View(viewLocation = "/views/dialogs/MultipleImportDialog.fxml",
        title = "批量导入文件",dialog = true,resizeable = false)
public class MultipleImportDialog extends AbstractView {

    public void showWithFolder(File folder) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }
        MultipleImportDialogController controller = this.getController();
        controller.setFolder(folder);
        this.show();
    }

}
