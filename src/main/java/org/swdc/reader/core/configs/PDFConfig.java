package org.swdc.reader.core.configs;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.FXProperties;
import org.swdc.reader.core.ReaderConfig;

/**
 * Created by lenovo on 2019/6/9.
 */
@Properties(value = "pdf.reader.properties",prefix = "pdf")
public class PDFConfig extends FXProperties implements ReaderConfig {

    @Getter
    private String name = "Adobe-PDF";


    @Getter
    @Setter
    @ConfigProp(name = "缓存大小",  type = PropType.NUMBER_SELECTABLE,
            value = "40", tooltip = "缓存一部分页面以加快翻阅时的载入速度", propName = "render-map-size")
    private Integer renderMapSize;

    @Getter
    @Setter
    @ConfigProp(name = "渲染质量", type = PropType.NUMBER,
            value = "4", tooltip = "高质量的渲染会有比较大的延时。", propName = "render-quality")
    private Float renderQuality;

    public void setName(String name) {
        return;
    }

}
