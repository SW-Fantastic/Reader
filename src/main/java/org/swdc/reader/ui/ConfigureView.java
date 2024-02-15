package org.swdc.reader.ui;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.controlsfx.control.PropertySheet;
import org.swdc.fx.FXResources;
import org.swdc.fx.config.ConfigViews;
import org.swdc.fx.view.AbstractView;
import org.swdc.fx.view.View;
import org.swdc.reader.ApplicationConfig;
import org.swdc.reader.core.configs.EpubConfig;
import org.swdc.reader.core.configs.PDFConfig;
import org.swdc.reader.core.configs.TextConfig;

import java.util.ResourceBundle;


@View(viewLocation = "/views/main/ConfigureView.fxml")
public class ConfigureView extends AbstractView {

    @Inject
    private TextConfig textConfig;

    @Inject
    private ApplicationConfig config;

    @Inject
    private EpubConfig epubConfig;

    @Inject
    private PDFConfig pdfConfig;

    @Inject
    private FXResources resources;

    @PostConstruct
    public void init() {

        ResourceBundle bundle = resources.getResourceBundle();

        TabPane configTab = this.findById("configTab");
        Tab general = new Tab(bundle.getString(LanguageKeys.KEY_CONF_PREF));

        ObservableList confGenerals = ConfigViews.parseConfigs(resources,config);
        PropertySheet generalConfSheet = new PropertySheet(confGenerals);
        generalConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));

        generalConfSheet.setModeSwitcherVisible(false);
        generalConfSheet.getStyleClass().add("prop-sheet");
        general.setContent(generalConfSheet);
        configTab.getTabs().add(general);

        Tab textConfTab = new Tab(bundle.getString(LanguageKeys.KEY_CONF_TEXT));

        ObservableList confText = ConfigViews.parseConfigs(resources,textConfig);
        PropertySheet  textConfSheet = new PropertySheet(confText);
        textConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));

        textConfSheet.setModeSwitcherVisible(false);
        textConfSheet.getStyleClass().add("prop-sheet");
        textConfTab.setContent(textConfSheet);
        configTab.getTabs().add(textConfTab);

        Tab epubConfTab = new Tab(bundle.getString(LanguageKeys.KEY_CONF_EPUB));

        ObservableList confEpub = ConfigViews.parseConfigs(resources,epubConfig);
        PropertySheet  epubConfSheet = new PropertySheet(confEpub);
        epubConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));

        epubConfSheet.setModeSwitcherVisible(false);
        epubConfSheet.getStyleClass().add("prop-sheet");
        epubConfTab.setContent(epubConfSheet);
        configTab.getTabs().add(epubConfTab);


        Tab pdfConfTab = new Tab(bundle.getString(LanguageKeys.KEY_CONF_PDF));

        ObservableList confPDF = ConfigViews.parseConfigs(resources,pdfConfig);
        PropertySheet  pdfConfSheet = new PropertySheet(confPDF);
        pdfConfSheet.setPropertyEditorFactory(ConfigViews.factory(resources));

        pdfConfSheet.setModeSwitcherVisible(false);
        pdfConfSheet.getStyleClass().add("prop-sheet");
        pdfConfTab.setContent(pdfConfSheet);
        configTab.getTabs().add(pdfConfTab);
    }


}
