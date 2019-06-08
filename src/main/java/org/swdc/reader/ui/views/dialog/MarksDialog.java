package org.swdc.reader.ui.views.dialog;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.aspects.anno.UIMethod;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/6/7.
 */
@FXMLView("/views/dialogs/MarksDialog.fxml")
public class MarksDialog extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    private Stage stage;

    @PostConstruct
    protected void initUI() throws Exception{
        UIUtils.configUI((BorderPane)this.getView(), config);
        Platform.runLater(() -> {
            stage = new Stage();
            Scene scene = new Scene(getView());
            stage.setTitle("书签");
            stage.setResizable(false);
            stage.initOwner(GUIState.getStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().addAll(AwsomeIconData.getImageIcons());
            stage.setScene(scene);
        });
    }

    @UIMethod
    public void show() {
        if(stage.isShowing()) {
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

}
