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
    private String name = "lang@tab-pdf";


    @Getter
    @Setter
    @ConfigProp(name = "lang@config-pdf-cache-size",  type = PropType.NUMBER_SELECTABLE,
            value = "40", tooltip = "lang@config-pdf-cache-size-tooltip", propName = "render-map-size")
    private Integer renderMapSize;

    @Getter
    @Setter
    @ConfigProp(name = "lang@config-pdf-quality", type = PropType.NUMBER,
            value = "4", tooltip = "lang@config-pdf-quality-tooltip", propName = "render-quality")
    private Float renderQuality;

    public void setName(String name) {
        return;
    }

}
