package org.swdc.reader.config;

import lombok.Getter;
import lombok.Setter;
import org.swdc.fx.anno.ConfigProp;
import org.swdc.fx.anno.PropType;
import org.swdc.fx.anno.Properties;
import org.swdc.fx.properties.DefaultUIConfigProp;
import org.swdc.fx.properties.LanguagePropertyEditor;

@Properties(value = "config.properties", prefix = "app")
public class AppConfig extends DefaultUIConfigProp {

    @Getter
    @Setter
    @ConfigProp(type = PropType.FOLDER_SELECT_IMPORTABLE,
            resolver = ThemeInstaller.class,
            value = "assets/theme",name = "lang@config-theme",
            tooltip = "lang@config-theme-tooltip",propName = "theme")
    private String theme;
    
    @Getter
    @Setter
    @ConfigProp(type = PropType.CUSTOM,value = "",
            editor = LanguagePropertyEditor.class,
            propName = "language",name = "lang@config-lang",
            tooltip = "lang@config-lang-tooltip")
    private String language;

}
