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
import org.swdc.reader.ui.ApplicationConfig;

/**
 * 文本阅读器的相关配置
 */
@Component
@ConfigurationProperties(prefix = "text")
@PropertySource("file:configs/text.reader.properties")
public class TextConfig implements ReaderConfig{

    @Getter
    private String name = "文本渲染";

    @Getter
    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 阅读字体
     */
    @Getter
    @Setter
    @ConfigProp(name = "阅读字体", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "configs/fonts", tooltip = "这是文本文件阅读的时候的字体样式", propName = "font-path")
    private String fontPath;

    /**
     * 背景图片
     */
    @Getter
    @Setter
    @ConfigProp(name = "阅读背景图", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "configs/readerResources/text",propName = "background-image", tooltip = "文本阅读的时候的背景图片")
    private String backgroundImage;

    /**
     * 是否启用背景图片
     */
    @Getter
    @Setter
    @ConfigProp(name = "使用阅读背景图", type = PropType.CHECK,
            value = "", tooltip = "是否在阅读时使用背景图", propName = "enable-background-image")
    private Boolean enableBackgroundImage;

    /**
     * 背景色
     */
    @Getter
    @Setter
    @ConfigProp(name = "阅读背景色", type = PropType.COLOR, value = "",
            tooltip = "阅读使用的背景颜色", propName = "background-color")
    private String backgroundColor;

    /**
     * 文本大小
     */
    @Getter
    @Setter
    @ConfigProp(name = "文字大小", type = PropType.NUMBER_SELECTABLE, value = "42",
            tooltip = "阅读时的文字大小", propName = "font-size")
    private Integer fontSize;

    /**
     * 字体颜色
     */
    @Getter
    @Setter
    @ConfigProp(name = "字体颜色", type = PropType.COLOR, value = "",
            tooltip = "字体的使用的颜色", propName = "font-color")
    private String fontColor;

    /**
     * 是否启用字体阴影
     */
    @Getter
    @Setter
    @ConfigProp(name = "渲染字体的阴影", type = PropType.CHECK,
            value = "", tooltip = "渲染字体的阴影",propName = "enable-text-shadow")
    private Boolean enableTextShadow;

    /**
     * 阴影色
     */
    @Getter
    @Setter
    @ConfigProp(name = "字体阴影的颜色", type = PropType.COLOR,
            value = "", tooltip = "字体的阴影色", propName = "shadow-color")
    private String shadowColor;

    /**
     * 为汉字注音
     */
    @Getter
    @Setter
    @ConfigProp(name="显示拼音", type = PropType.CHECK,
            value = "", tooltip = "是否为汉字注音", propName = "show-pin-yin")
    private Boolean showPinYin;

}
