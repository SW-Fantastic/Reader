package org.swdc.reader.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.FXProperties;
import org.swdc.reader.core.ReaderConfig;

/**
 * 文本阅读器的相关配置
 */
@Properties(value = "text.reader.properties", prefix = "text")
public class TextConfig extends FXProperties implements ReaderConfig{

    @Getter
    private String name = "lang@tab-text-render";


    /**
     * 阅读字体
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font",
            type = PropType.FILE_SELECT_IMPORTABLE,
            value = "assets/fonts",
            tooltip = "lang@config-text-font-tooltip", propName = "font-path")
    private String fontPath;

    /**
     * 背景图片
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-back", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "assets/readerBackground/",
            propName = "background-image", tooltip = "lang@config-text-back-tooltip")
    private String backgroundImage;

    /**
     * 是否启用背景图片
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-enable-bg", type = PropType.CHECK,
            value = "", tooltip = "lang@config-text-enable-bg-tooltip",
            propName = "enable-background-image")
    private Boolean enableBackgroundImage;

    /**
     * 背景色
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-bg-color", type = PropType.COLOR, value = "",
            tooltip = "lang@config-text-bg-color-tooltip", propName = "background-color")
    private String backgroundColor;

    /**
     * 文本大小
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font-size",
            type = PropType.NUMBER_SELECTABLE, value = "42",
            tooltip = "lang@config-text-font-size-tooltip", propName = "font-size")
    private Integer fontSize;

    /**
     * 字体颜色
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font-color",
            type = PropType.COLOR, value = "",
            tooltip = "lang@config-text-font-color-tooltip",
            propName = "font-color")
    private String fontColor;

    /**
     * 是否启用字体阴影
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font-shadow", type = PropType.CHECK,
            value = "", tooltip = "lang@config-text-font-shadow-tooltip"
            ,propName = "enable-text-shadow")
    private Boolean enableTextShadow;

    /**
     * 阴影色
     */
    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-shadow-color",
            type = PropType.COLOR,
            value = "", tooltip = "lang@config-text-shadow-color-tooltip",
            propName = "shadow-color")
    private String shadowColor;

    /**
     * 为汉字注音
     */
    @Getter
    @Setter
    @ConfigProp(name="lang@config-text-pinyin", type = PropType.CHECK,
            value = "", tooltip = "lang@config-text-pinyin-tooltip", propName = "show-pin-yin")
    private Boolean showPinYin;

    @Getter
    @Setter
    @ConfigProp(name = "lang@config-text-pageby",type = PropType.CHECK,value = "",
            tooltip = "lang@config-text-pageby-tooltip",propName = "divide-by-page")
    private Boolean divideByChapter;

    public void setName(String name) {
        return;
    }

}
