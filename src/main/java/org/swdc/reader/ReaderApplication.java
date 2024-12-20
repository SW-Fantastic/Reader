package org.swdc.reader;

import org.swdc.data.EMFProviderFactory;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.fx.FXApplication;
import org.swdc.fx.FXResources;
import org.swdc.fx.SWFXApplication;
import org.swdc.platforms.NativePlatform;
import org.swdc.reader.entity.EMFProviderImpl;
import org.swdc.reader.ui.MainView;

import java.io.File;

/**
 * 应用启动和依赖控制的类
 * @author SW-Fantastic
 */
@SWFXApplication(assetsFolder = "./assets",
        splash = SplashScreen.class,
        configs = { ApplicationConfig.class },
        icons = { "book16.png","book24.png","book32.png","book48.png","book64.png","book72.png" })
public class ReaderApplication extends FXApplication {

    @Override
    public void onConfig(EnvironmentLoader loader) {
        loader.withProvider(EMFProviderImpl.class);
    }

    @Override
    public void onStarted(DependencyContext dependencyContext) {

        FXResources resources = dependencyContext.getByClass(FXResources.class);
        //PdfiumPlatform.initializePdfium(resources.getAssetsFolder());
        NativePlatform.initializePlatform(resources.getAssetsFolder());
        EMFProviderFactory factory = dependencyContext.getByClass(EMFProviderFactory.class);
        factory.create();

        MainView view = dependencyContext.getByClass(MainView.class);
        view.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
