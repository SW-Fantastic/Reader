package org.swdc.reader.ui.controller;

import javafx.fxml.FXML;
import org.swdc.fx.FXController;
import org.swdc.fx.properties.FXProperties;
import org.swdc.reader.config.AppConfig;
import org.swdc.reader.core.ReaderConfig;
import org.swdc.reader.ui.view.ConfigView;

import java.util.List;

public class ConfigController extends FXController {

    @FXML
    public void saveConfigs() {
        List<FXProperties> properties = getScoped(FXProperties.class);
        for (FXProperties prop: properties) {
            if (!(prop instanceof ReaderConfig)) {
                continue;
            }
            prop.saveProperties();
        }
        AppConfig config = findComponent(AppConfig.class);
        config.saveProperties();
        ConfigView configView = getView();
        configView.showToast("配置已经保存！部分配置需要重启才能生效。");
    }

}
