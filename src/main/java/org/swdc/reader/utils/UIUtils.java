package org.swdc.reader.utils;

import de.felixroske.jfxsupport.GUIState;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import org.springframework.core.io.ClassPathResource;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.ui.AwsomeIconData;

import java.util.Optional;

/**
 * Created by lenovo on 2019/5/19.
 */
public class UIUtils {

    /**
     * 初始化node的CSS样式
     * @param pane 被初始化的界面
     * @param config 配置
     * @throws Exception
     */
    public static void configUI(Pane pane, ApplicationConfig config) throws Exception {
        if(config.getTheme().equals("")||config.getTheme().equals("default")){
            pane.getStylesheets().add(new ClassPathResource("style/default.css").getURL().toExternalForm());
        }else{
            pane.getStylesheets().add("file:configs/theme/"+config.getTheme()+"/"+config.getTheme()+".css");
        }
    }

    public static <T> T findById(String id, ObservableList<Node> list){
        for (Node node:list) {
            if(node.getId().equals(id)){
                return (T)node;
            }
        }
        return null;
    }

    public static Optional<ButtonType> showAlertDialog(String content, String title, Alert.AlertType type, ApplicationConfig config) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.initOwner(GUIState.getStage());
        try {
            UIUtils.configUI(alert.getDialogPane(), config);
            return alert.showAndWait();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
