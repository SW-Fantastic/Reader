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
    @ConfigProp(name = "阅读字体", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "configs/fonts", tooltip = "这是文本文件阅读的时候的字体样式")
    private String fontPath;

    /**
     * 背景图片
     */
    @Getter
    @Setter
    @ConfigProp(name = "阅读背景图", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "configs/readerResources/text", tooltip = "文本阅读的时候的背景图片")
    private String backgroundImage;

    /**
     * 是否启用背景图片
     */
    @Getter
    @Setter
    @ConfigProp(name = "使用阅读背景图", type = PropType.CHECK,
            value = "", tooltip = "是否在阅读时使用背景图")
    private Boolean enableBackgroundImage;

    /**
     * 背景色
     */
    @Getter
    @Setter
    @ConfigProp(name = "阅读背景色", type = PropType.COLOR, value = "", tooltip = "阅读使用的背景颜色")
    private String backgroundColor;

    /**
     * 文本大小
     */
    @Getter
    @Setter
    @ConfigProp(name = "文字大小", type = PropType.NUMBER_SELECTABLE, value = "42",
            tooltip = "阅读时的文字大小")
    private Integer fontSize;

    /**
     * 字体颜色
     */
    @Getter
    @Setter
    @ConfigProp(name = "字体颜色", type = PropType.COLOR, value = "", tooltip = "字体的使用的颜色")
    private String fontColor;

    /**
     * 是否启用字体阴影
     */
    @Getter
    @Setter
    @ConfigProp(name = "渲染字体的阴影", type = PropType.CHECK, value = "", tooltip = "渲染字体的阴影")
    private Boolean enableTextShadow;

    /**
     * 阴影色
     */
    @Getter
    @Setter
    @ConfigProp(name = "字体阴影的颜色", type = PropType.COLOR, value = "", tooltip = "字体的阴影色")
    private String shadowColor;

}
