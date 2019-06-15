package org.swdc.reader.ui.controllers;

import de.felixroske.jfxsupport.FXMLController;
import de.felixroske.jfxsupport.GUIState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;
import lombok.extern.apachecommons.CommonsLog;
import org.controlsfx.control.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.core.ReaderConfig;
import org.swdc.reader.event.RestartEvent;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.utils.DataUtil;
import org.swdc.reader.utils.UIUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by lenovo on 2019/6/15.
 */
@FXMLController
@CommonsLog
public class ConfigViewController implements Initializable {

    @Autowired
    private List<ReaderConfig> configs;

    @Autowired
    private ApplicationConfig config;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void saveConfigs () {
        try {
            for (ReaderConfig conf : configs) {
                DataUtil.saveConfigFile(conf);
            }
            DataUtil.saveConfigFile(config);
            UIUtils.showAlertDialog("主题设置需要重新启动应用才会生效，如果你修改了主题，那么请重启，否则可以忽略此提示。是否要重启应用？"
                    ,"提示", Alert.AlertType.CONFIRMATION,config).ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    config.publishEvent(new RestartEvent());
                } else {
                    Notifications.create()
                            .owner(GUIState.getStage())
                            .hideCloseButton()
                            .hideAfter(Duration.millis(2000))
                            .text("配置已经保存。")
                            .position(Pos.CENTER)
                            .show();
                }
            });
        } catch (Exception e) {
            log.error(e);
        }
    }

}
