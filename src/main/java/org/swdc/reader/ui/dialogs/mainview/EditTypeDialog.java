package org.swdc.reader.ui.dialogs.mainview;

import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.LanguageKeys;

@View(viewLocation = "/views/dialogs/EditType.fxml",
        title = LanguageKeys.BOOK_TYPE_EDIT,dialog = true,resizeable = false)
public class EditTypeDialog extends AbstractView {

    public void showWithType(BookType type) {
        EditTypeDialogController ctrl = this.getController();
        ctrl.setType(type);
        this.show();
    }

}
