package org.swdc.reader.ui.view;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import org.swdc.fx.FXView;
import org.swdc.fx.anno.View;
import org.swdc.fx.properties.FXProperties;
import org.swdc.reader.config.AppConfig;
import org.swdc.reader.core.ReaderConfig;

import java.util.List;

@View(stage = false)
public class ConfigView extends FXView {

    @Override
    public void initialize() {
        BorderPane pane = getView();
        TabPane tabs = (TabPane) pane.getCenter();
        List<FXProperties> properties = getScoped(FXProperties.class);
        for (FXProperties prop: properties) {
            if (!(prop instanceof ReaderConfig)) {
                continue;
            }
            ReaderConfig config = (ReaderConfig)prop;
            tabs.getTabs().add(new Tab(this.i18n(config.getName()),prop.getEditor()));
        }
        tabs.getTabs().add(new Tab(this.i18n("lang@tab-config"),findComponent(AppConfig.class).getEditor()));
    }
}
