package org.swdc.reader.ui.view;

import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.controller.ReadController;

@View(stage = false)
public class ReadView extends FXView {

    public Book getOpenedBook() {
        ReadController controller = getLoader().getController();
        if (controller.getCurrentReader() != null) {
            return controller.getCurrentReader().getBook();
        }
        return null;
    }

    public void closeBook() {
        ReadController controller = getLoader().getController();
        if (controller.getCurrentReader() != null) {
            controller.closeOpenedBook();
        }
    }

}
