package org.swdc.reader.core.configs;


import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.CheckEditor;
import org.swdc.fx.config.editors.ColorEditor;
import org.swdc.fx.config.editors.FileSelectionEditor;
import org.swdc.fx.config.editors.NumberEditor;


/**
 * 文本阅读器的相关配置
 */
@ConfigureSource(value = "assets/text.reader.properties",handler = PropertiesHandler.class)
public class TextConfig extends AbstractConfig {

    private String name = "Txt文档";

    @PropEditor(name = "显示拼音",
            description = "在文本上方标注拼音 。",
            editor = CheckEditor.class)
    @Property("show-pinyin")
    private Boolean showPinYin;

    @PropEditor(name = "字体大小",
            description = "改变阅读的字体大小。",
            editor = NumberEditor.class,
            resource = "16,24")
    @Property("font-size")
    private Integer fontSize;

    @PropEditor(name = "背景图",
            description = "阅读的时候的背景图片。",
            editor = FileSelectionEditor.class,
            resource = "pageBg")
    @Property("background-image")
    private String backgroundImage;


    @PropEditor(name = "字体样式",
            description = "阅读的时候所使用的字体文件。",
            editor = FileSelectionEditor.class,
            resource = "fonts")
    @Property("font-path")
    private String fontFileName;

    @PropEditor(name = "文字颜色",
            description = "文字的颜色。",
            editor = ColorEditor.class)
    @Property("font-color")
    private String fontColor;

    @PropEditor(name = "背景颜色",
            description = "阅读器的背景色。",
            editor = ColorEditor.class)
    @Property("background-color")
    private String backgroundColor;

    @PropEditor(name = "启用背景图片",
            description = "在阅读器中显示背景图片。",
            editor = CheckEditor.class)
    @Property("enable-background")
    private Boolean enableBackgroundImage;

    @PropEditor(name = "启用文字阴影",
            description = "在阅读器中为文本添加阴影。",
            editor = CheckEditor.class)
    @Property("enable-text-shadow")
    private Boolean enableTextShadow;

    @PropEditor(name = "文本阴影颜色",
            description = "文本阴影色。",
            editor = ColorEditor.class)
    @Property("text-shadow-color")
    private String shadowColor;

    public TextConfig() {
        super();
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

    public Boolean getEnableTextShadow() {
        return enableTextShadow;
    }

    public void setEnableTextShadow(Boolean enableTextShadow) {
        this.enableTextShadow = enableTextShadow;
    }

    public Boolean getEnableBackgroundImage() {
        return enableBackgroundImage;
    }

    public void setEnableBackgroundImage(Boolean enableBackgroundImage) {
        this.enableBackgroundImage = enableBackgroundImage;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontFileName() {
        return fontFileName;
    }

    public void setFontFileName(String fontFileName) {
        this.fontFileName = fontFileName;
    }

    public Boolean getShowPinYin() {
        return showPinYin;
    }

    public void setShowPinYin(Boolean showPinYin) {
        this.showPinYin = showPinYin;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }



    /**
     * 阅读字体
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font",
            type = PropType.FILE_SELECT_IMPORTABLE,
            value = "assets/fonts",
            tooltip = "lang@config-text-font-tooltip", propName = "font-path")
    private String fontPath;*/

    /**
     * 背景图片
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-back", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "assets/readerBackground/",
            propName = "background-image", tooltip = "lang@config-text-back-tooltip")
    private String backgroundImage;*/

    /**
     * 是否启用背景图片
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-enable-bg", type = PropType.CHECK,
            value = "", tooltip = "lang@config-text-enable-bg-tooltip",
            propName = "enable-background-image")
    private Boolean enableBackgroundImage;*/

    /**
     * 背景色
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-bg-color", type = PropType.COLOR, value = "",
            tooltip = "lang@config-text-bg-color-tooltip", propName = "background-color")
    private String backgroundColor;*/

    /**
     * 文本大小
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font-size",
            type = PropType.NUMBER_SELECTABLE, value = "42",
            tooltip = "lang@config-text-font-size-tooltip", propName = "font-size")
    private Integer fontSize;*/

    /**
     * 字体颜色
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font-color",
            type = PropType.COLOR, value = "",
            tooltip = "lang@config-text-font-color-tooltip",
            propName = "font-color")
    private String fontColor;*/

    /**
     * 是否启用字体阴影
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-font-shadow", type = PropType.CHECK,
            value = "", tooltip = "lang@config-text-font-shadow-tooltip"
            ,propName = "enable-text-shadow")
    private Boolean enableTextShadow;*/

    /**
     * 阴影色
     */
    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-shadow-color",
            type = PropType.COLOR,
            value = "", tooltip = "lang@config-text-shadow-color-tooltip",
            propName = "shadow-color")
    private String shadowColor;*/

    /**
     * 为汉字注音
     */
    /*@Getter
    @Setter
    @ConfigProp(name="lang@config-text-pinyin", type = PropType.CHECK,
            value = "", tooltip = "lang@config-text-pinyin-tooltip", propName = "show-pin-yin")
    private Boolean showPinYin;*/

    /*@Getter
    @Setter
    @ConfigProp(name = "lang@config-text-pageby",type = PropType.CHECK,value = "",
            tooltip = "lang@config-text-pageby-tooltip",propName = "divide-by-page")
    private Boolean divideByChapter;*/

}
