package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.control.Accordion;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanPropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/6/8.
 */
@FXMLView("/views/ConfigView.fxml")
public class ConfigView extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    @Autowired
    private TextConfig textConfig;

    @PostConstruct
    protected void initUI() throws Exception {
        UIUtils.configUI((BorderPane)getView(), config);
        BorderPane pane = (BorderPane)getView();
        TabPane configTabs = (TabPane) pane.getCenter();

        PropertySheet configSheet = new PropertySheet(UIUtils.getProperties(config));
        configSheet.setPropertyEditorFactory(UIUtils::getEditor);
        configSheet.setModeSwitcherVisible(false);
        configTabs.getTabs().add(new Tab("通用配置", configSheet));

        PropertySheet textReaderSheet = new PropertySheet(UIUtils.getProperties(textConfig));
        textReaderSheet.setPropertyEditorFactory(UIUtils::getEditor);
        textReaderSheet.setModeSwitcherVisible(false);
        configTabs.getTabs().add(new Tab("文本", textReaderSheet));
    }

}
