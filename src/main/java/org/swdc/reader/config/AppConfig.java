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
            resolver = ThemeInstaller.class,
            value = "assets/theme",name = "主题",
            tooltip = "界面主题",propName = "theme")
    private String theme;

}
