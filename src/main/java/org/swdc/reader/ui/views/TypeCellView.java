package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.ui.views.dialog.TypeEditViewDialog;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 *
 */
@Scope("prototype")
@FXMLView("/views/cells/TypeCell.fxml")
public class TypeCellView extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private TypeEditViewDialog editViewDialog;

    protected BookType type;

    @PostConstruct
    protected void initUI() throws Exception{
        UIUtils.configUI((BorderPane)this.getView(),config);
        BorderPane pane = (BorderPane)this.getView();
        HBox centerBox = (HBox)pane.getCenter();
        HBox labelBox = UIUtils.findById("boxBtn",centerBox.getChildren());
        Button typeName = UIUtils.findById("edit", labelBox.getChildren());
        typeName.setOnAction(this::onTypeEdit);
        typeName.setFont(AwsomeIconData.getFontIconSmall());
        typeName.setText("" + AwsomeIconData.getAwesomeMap().get("pencil"));
    }

    public void setType(BookType type){
        this.type = type;
        BorderPane pane = (BorderPane)this.getView();
        HBox centerBox = (HBox)pane.getCenter();
        HBox labelBox = UIUtils.findById("labelBox",centerBox.getChildren());
        Label typeName = UIUtils.findById("typeName", labelBox.getChildren());
        if (type != null) {
            typeName.setText(type.getName());
        } else {
            typeName.setText("");
        }
    }

    private void onTypeEdit(ActionEvent event) {
        if (type == null) {
            return;
        }
        editViewDialog.setType(type);
        editViewDialog.show();
    }

}
