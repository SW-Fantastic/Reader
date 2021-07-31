package org.swdc.reader.core.configs;


import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.ColorEditor;

/**
 * Created by lenovo on 2019/6/13.
 */
@ConfigureSource(value = "assets/epub.reader.properties", handler = PropertiesHandler.class)
public class EpubConfig extends AbstractConfig {

    private String name = "EPUB电子出版物";

    @PropEditor(name = "链接颜色",
            description = "EPUB文档可以存在超链接，这里可以指定超链接字体的颜色。",
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
