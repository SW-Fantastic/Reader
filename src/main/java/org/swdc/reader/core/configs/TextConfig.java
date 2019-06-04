package org.swdc.reader.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.swdc.reader.ui.ApplicationConfig;

/**
 * 文本阅读器的相关配置
 */
@Component
@ConfigurationProperties(prefix = "text")
@PropertySource("file:configs/text.reader.properties")
public class TextConfig {

    @Getter
    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 阅读字体
     */
    @Getter
    @Setter
    private String fontPath;

    /**
     * 背景图片
     */
    @Getter
    @Setter
    private String backgroundImage;

    /**
     * 是否启用背景图片
     */
    @Getter
    @Setter
    private Boolean enableBackgroundImage;

    /**
     * 背景色
     */
    @Getter
    @Setter
    private String backgroundColor;

    /**
     * 文本大小
     */
    @Getter
    @Setter
    private Integer fontSize;

    /**
     * 字体颜色
     */
    @Getter
    @Setter
    private String fontColor;

    /**
     * 是否启用字体阴影
     */
    @Getter
    @Setter
    private Boolean enableTextShadow;

    /**
     * 阴影色
     */
    @Getter
    @Setter
    private String shadowColor;

}
