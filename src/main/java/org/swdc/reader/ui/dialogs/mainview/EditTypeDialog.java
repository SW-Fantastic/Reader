package org.swdc.reader.ui.dialogs.mainview;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.reader.entity.BookType;

@View(viewLocation = "/views/dialogs/EditType.fxml",
        title = "修改分类",dialog = true,resizeable = false)
public class EditTypeDialog extends AbstractView {

    public void showWithType(BookType type) {
        EditTypeDialogController ctrl = this.getController();
        ctrl.setType(type);
        this.show();
    }

}
