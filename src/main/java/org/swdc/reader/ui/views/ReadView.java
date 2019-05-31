package org.swdc.reader.ui.views;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.utils.UIUtils;

import javax.annotation.PostConstruct;

/**
 * Created by lenovo on 2019/5/31.
 */
@FXMLView("/views/ReadView.fxml")
public class ReadView extends AbstractFxmlView {

    @Autowired
    private ApplicationConfig config;

    @PostConstruct
    protected void initUI() throws Exception {
        UIUtils.configUI((BorderPane)this.getView(), config);
    }

}
