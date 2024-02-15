package org.swdc.reader.core.configs;


import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.NumberEditor;
import org.swdc.reader.ui.LanguageKeys;

/**
 * Created by lenovo on 2019/6/9.
 */
@ConfigureSource(value = "assets/pdf.reader.properties",handler = PropertiesHandler.class)
public class PDFConfig extends AbstractConfig {

    private String name = "AdobePDF 文档";

    @PropEditor(name = LanguageKeys.CONF_CACHE_SIZE,
            description = LanguageKeys.CONF_CACHE_SIZE_DESC,
            editor = NumberEditor.class,
            resource = "12,24")
    @Property("reader-map-size")
    private Integer renderMapSize;

    @PropEditor(name = LanguageKeys.CONF_RENDER_QUALITY,
            description = LanguageKeys.CONF_RENDER_QUALITY_DESC,
            editor = NumberEditor.class,
            resource = "1,4")
    @Property("reader-quality")
    private Float renderQuality;

    public Integer getRenderMapSize() {
        return renderMapSize;
    }

    public void setRenderMapSize(Integer renderMapSize) {
        this.renderMapSize = renderMapSize;
    }

    public Float getRenderQuality() {
        return renderQuality;
    }

    public void setRenderQuality(Float renderQuality) {
        this.renderQuality = renderQuality;
    }

    public void setName(String name) {
        return;
    }


}
