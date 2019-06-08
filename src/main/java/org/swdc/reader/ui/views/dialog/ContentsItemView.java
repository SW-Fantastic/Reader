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
import org.swdc.reader.anno.UIMethod;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 *  用来显示目录的对话框
 */
@FXMLView("/views/dialogs/ContentsView.fxml")
public class ContentsItemView extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    private Stage stage;

    @PostConstruct
    protected void initUI() throws Exception{
        UIUtils.configUI((BorderPane)this.getView(),config);
        Platform.runLater(() -> {
            stage = new Stage();
            Scene scene = new Scene(this.getView());
            stage.setScene(scene);
            stage.initOwner(GUIState.getStage());
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().addAll(AwsomeIconData.getImageIcons());
            stage.setTitle("目录");
        });
    }

    @UIMethod
    public void show() {
        if (this.stage.isShowing()) {
            stage.requestFocus();
        } else {
            stage.show();
        }
    }

    @UIMethod
    public void close() {
        if (this.stage.isShowing()) {
            stage.close();
        }
    }

}
