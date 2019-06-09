package org.swdc.reader.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.swdc.reader.anno.ConfigProp;
import org.swdc.reader.anno.PropType;
import org.swdc.reader.ui.ApplicationConfig;

/**
 * Created by lenovo on 2019/6/9.
 */
@PropertySource("file:configs/pdf.reader.properties")
@ConfigurationProperties(prefix = "pdf")
@Component
public class PDFConfig {

    @Getter
    @Autowired
    private ApplicationConfig config;

    @Getter
    @Setter
    @ConfigProp(name = "缓冲大小",  type = PropType.NUMBER_SELECTABLE,
            value = "40", tooltip = "缓存一部分页面以加快翻阅时的载入速度")
    private Integer renderMapSize;

    @Getter
    @Setter
    @ConfigProp(name = "渲染质量", type = PropType.NUMBER,
            value = "4", tooltip = "高质量的渲染会有比较大的延时。")
    private Float renderQuality;

}
