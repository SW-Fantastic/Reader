package org.swdc.reader.ui.dialogs.mainview;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.scene.control.Button;
import org.swdc.fx.font.FontSize;
import org.swdc.fx.font.FontawsomeService;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.LanguageKeys;

@View(viewLocation = "/views/dialogs/BookEditDialog.fxml",
        title = LanguageKeys.BOOK_DETAILS,dialog = true,resizeable = false)
public class BookEditDialog extends AbstractView {

    @Inject
    private FontawsomeService fontawsomeService;

    public void showWithBook(Book book) {
        if (book == null) {
            return;
        }
        BookEditDialogController controller = this.getController();
        controller.setBook(book);
        this.show();
    }

    @Override
    public void hide() {
        BookEditDialogController controller = this.getController();
        controller.cleanOnClose();
        super.hide();
    }
}
