package org.swdc.reader.ui.view.dialogs;

import javafx.scene.control.TextField;
import lombok.Getter;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.Scope;
import org.swdc.fx.anno.ScopeType;
import org.swdc.fx.anno.View;
import org.swdc.reader.entity.BookType;


/**
 * Created by lenovo on 2019/6/8.
 */
@Scope(ScopeType.MULTI)
@View(title = "lang@dialog-category",dialog = true)
public class TypeEditViewDialog extends FXView {

    @Getter
    private BookType type;

    public void setType(BookType type) {
        TextField txtName = this.findById("txtName");
        TextField txtCount = this.findById("txtCount");
        txtName.setText(type.getName());
        txtCount.setText(type.getBooks().size() + "");
        this.type = type;
    }

}
