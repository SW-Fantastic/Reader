package org.swdc.reader.core.configs;


import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.ColorEditor;
import org.swdc.reader.ui.LanguageKeys;

/**
 * Created by lenovo on 2019/6/13.
 */
@ConfigureSource(value = "assets/epub.reader.properties", handler = PropertiesHandler.class)
public class EpubConfig extends AbstractConfig {

    private String name = "EPUB电子出版物";

    @PropEditor(name = LanguageKeys.CONF_EPUB_LINK,
            description = LanguageKeys.CONF_EPUB_LINK_DESC,
            editor = ColorEditor.class)
    @Property("link-color")
    private String linkColor;

    public String getLinkColor() {
        return linkColor;
    }

    public void setLinkColor(String linkColor) {
        this.linkColor = linkColor;
    }

}
