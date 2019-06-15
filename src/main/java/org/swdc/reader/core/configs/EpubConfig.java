package org.swdc.reader.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.swdc.reader.anno.ConfigProp;
import org.swdc.reader.anno.PropType;
import org.swdc.reader.core.ReaderConfig;
import org.swdc.reader.entity.Book;
import org.swdc.reader.ui.ApplicationConfig;

/**
 * Created by lenovo on 2019/6/13.
 */
@PropertySource("file:configs/epub.reader.properties")
@ConfigurationProperties(prefix = "epub")
@Component
public class EpubConfig implements ReaderConfig {

    @Getter
    @Autowired
    private ApplicationConfig config;

    @Getter
    private String name = "E-Public";

    @Getter
    @Setter
    @ConfigProp(type = PropType.COLOR, name = "链接颜色",
            value = "", tooltip = "超级链接的颜色")
    private String linkColor;

    @Getter
    @Setter
    @ConfigProp(type = PropType.CHECK, name = "启用链接跳转",
            value = "", tooltip = "可以使用超链接在页面间跳转")
    private Boolean enableHyperLinks;

}
