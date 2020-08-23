package org.swdc.reader.config;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.DefaultUIConfigProp;

@Properties(value = "config.properties", prefix = "app")
public class AppConfig extends DefaultUIConfigProp {

    @Getter
    @Setter
    @ConfigProp(type = PropType.FOLDER_SELECT_IMPORTABLE,
            value = "assets/theme",name = "主题",
            tooltip = "界面主题",propName = "theme")
    private String theme;

    @Getter
    @Setter
    @ConfigProp(name = "背景图片", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "configs/images", tooltip = "使用的背景图片，如果对主题的图片不满意可以修改", propName = "background")
    private String background;

}
