package org.swdc.reader.ui.view.dialogs;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.reader.core.BookReader;

@View(title = "添加书签",dialog = true)
public class MarkCreateDialog extends FXView {

    @Setter
    @Getter
    private BookReader book;

}
