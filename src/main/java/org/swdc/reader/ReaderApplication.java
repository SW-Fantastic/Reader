package org.swdc.reader;

import javafx.application.Application;
import javafx.scene.image.Image;
import org.swdc.fx.FXApplication;
import org.swdc.fx.FXSplash;
import org.swdc.fx.anno.SFXApplication;
import org.swdc.fx.container.ApplicationContainer;
import org.swdc.fx.properties.ConfigManager;
import org.swdc.reader.config.AppConfig;
import org.swdc.reader.core.ext.ExternResolverManager;
import org.swdc.reader.core.readers.ReaderManager;
import org.swdc.reader.ui.view.MainView;

import java.util.ArrayList;
import java.util.List;

@SFXApplication(singleton = true,mainView = MainView.class, splash = FXSplash.class)
public class ReaderApplication extends FXApplication {


    public static void main(String[] args) {
        Application.launch(ReaderApplication.class, args);
    }

    @Override
    protected void onLaunch(ConfigManager configManager) {
        configManager.register(AppConfig.class);
    }

    @Override
    protected void onStart(ApplicationContainer container) {
        container.register(ReaderManager.class);
        container.register(ExternResolverManager.class);
    }

    @Override
    protected List<Image> loadIcons() {
        try {
            Module module = this.getClass().getModule();

            ArrayList<Image> icons = new ArrayList<>();
            icons.add(new Image(module.getResourceAsStream("appicons/book16.png")));
            icons.add(new Image(module.getResourceAsStream("appicons/book24.png")));
            icons.add(new Image(module.getResourceAsStream("appicons/book32.png")));
            icons.add(new Image(module.getResourceAsStream("appicons/book48.png")));
            icons.add(new Image(module.getResourceAsStream("appicons/book64.png")));
            icons.add(new Image(module.getResourceAsStream("appicons/book72.png")));
            return icons;
        } catch (Exception ex) {
            return super.loadIcons();
        }
    }
}
