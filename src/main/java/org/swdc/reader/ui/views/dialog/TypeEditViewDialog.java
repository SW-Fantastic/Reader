package org.swdc.reader.ui.views.dialog;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.aspects.anno.UIMethod;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/6/8.
 */
@FXMLView("/views/dialogs/TypeEditView.fxml")
public class TypeEditViewDialog extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    @Getter
    private BookType type;

    private Stage stage;

    @PostConstruct
    public void initUI() throws Exception{
        UIUtils.configUI((BorderPane)getView(), config);
        Platform.runLater(() -> {
            stage = new Stage();
            Scene scene = new Scene(getView());
            stage.setScene(scene);
            stage.setTitle("分类编辑");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(GUIState.getStage());
            stage.getIcons().addAll(AwsomeIconData.getImageIcons());
        });
    }

    @UIMethod
    public void show() {
        if (stage.isShowing()) {
                stage.requestFocus();
        } else {
                stage.show();
        }
    }

    @UIMethod
    public void close() {
        if (stage.isShowing()) {
            stage.close();
        }
    }

    public void setType(BookType type) {
        BorderPane pane = (BorderPane)getView();
        GridPane gridPane = (GridPane)pane.getCenter();
        TextField txtName = (TextField)gridPane.lookup("#txtName");
        TextField txtCount = (TextField)gridPane.lookup("#txtCount");
        txtName.setText(type.getName());
        txtCount.setText(type.getBooks().size() + "");
        this.type = type;
    }

}
