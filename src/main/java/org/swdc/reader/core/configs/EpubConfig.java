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
    private String name = "E-Public";

    @Getter
    @Setter
    @ConfigProp(type = PropType.COLOR, name = "链接颜色",
            value = "", tooltip = "超级链接的颜色", propName = "link-color")
    private String linkColor;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CHECK, name = "启用链接跳转",
            value = "", tooltip = "可以使用超链接在页面间跳转",propName = "enable-hyper-links")
    private Boolean enableHyperLinks;

    public void setName(String name) {
        return;
    }
}
