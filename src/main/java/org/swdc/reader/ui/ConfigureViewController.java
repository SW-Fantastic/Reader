package org.swdc.reader.ui;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;
import org.swdc.fx.view.Toast;
import org.swdc.reader.ApplicationConfig;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.core.configs.TextConfig;
import org.swdc.reader.events.ConfigChangedEvent;

public class ConfigureViewController {

    @Inject
    private ApplicationConfig config;

    @Inject
    private TextConfig textConfig;

    @Inject
    private EpubConfig epubConfig;

    @Inject
    private PDFConfig pdfConfig;

    @Inject
    private ConfigureView view;

    @Inject
    private Logger logger;

    @Inject
    private FXResources resources;

    public void saveConfigures() {
        try {
            config.save();
            textConfig.save();
            epubConfig.save();
            pdfConfig.save();
            Toast.showMessage(resources.getResourceBundle().getString(LanguageKeys.KEY_CONF_SAVED));
            view.emit(new ConfigChangedEvent(null));
        } catch (Exception e) {
            logger.error(resources.getResourceBundle().getString(LanguageKeys.KEY_CONF_SAVE_FAILED),e);
        }
    }

}
