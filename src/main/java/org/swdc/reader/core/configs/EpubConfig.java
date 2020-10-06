package org.swdc.reader.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.FXProperties;
import org.swdc.reader.core.ReaderConfig;

/**
 * Created by lenovo on 2019/6/13.
 */
@Properties(value = "epub.reader.properties",prefix = "epub")
public class EpubConfig extends FXProperties implements ReaderConfig {

    @Getter
    private String name = "lang@tab-epub";

    @Getter
    @Setter
    @ConfigProp(type = PropType.COLOR, name = "lang@config-epub-hyperlink-color",
            value = "", tooltip = "lang@config-epub-hyperlink-color-tooltip", propName = "link-color")
    private String linkColor;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CHECK, name = "lang@config-epub-hyperlink-enable",
            value = "", tooltip = "lang@config-epub-hyperlink-enable-tooltip",propName = "enable-hyper-links")
    private Boolean enableHyperLinks;

    public void setName(String name) {
        return;
    }
}
