package org.swdc.reader.core.configs;


import org.swdc.config.AbstractConfig;
import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.annotations.Property;
import org.swdc.config.configs.PropertiesHandler;
import org.swdc.fx.config.PropEditor;
import org.swdc.fx.config.editors.NumberEditor;

/**
 * Created by lenovo on 2019/6/9.
 */
@ConfigureSource(value = "assets/pdf.reader.properties",handler = PropertiesHandler.class)
public class PDFConfig extends AbstractConfig {

    private String name = "AdobePDF 文档";

    @PropEditor(name = "缓存大小",
            description = "一定程度影响性能。",
            editor = NumberEditor.class,
            resource = "12,24")
    @Property("reader-map-size")
    private Integer renderMapSize;

    @PropEditor(name = "渲染质量",
            description = "页面的清晰度。",
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
