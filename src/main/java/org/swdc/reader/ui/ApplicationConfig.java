package org.swdc.reader.ui;

/**
 *
 */

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.swdc.reader.anno.ConfigProp;
import org.swdc.reader.anno.PropType;

@PropertySource("file:configs/config.properties")
@ConfigurationProperties(prefix = "app")
@Component
public class ApplicationConfig {

    @Getter
    private static final String configLocation = "file:configs/";

    @Autowired
    private ApplicationContext context;

    @Getter
    @Setter
    @ConfigProp(name = "主题", type = PropType.FOLDER_SELECT_IMPORTABLE,
            value = "configs/theme", tooltip = "整体的配色和风格设置")
    private String theme;

    @Getter
    @Setter
    @ConfigProp(name = "背景图片", type = PropType.FILE_SELECT_IMPORTABLE,
            value = "configs/images", tooltip = "使用的背景图片，如果对主题的图片不满意可以修改")
    private String background;

    public void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }

    public <T> T getComponent(Class<T> clazz) {
        Scope scope = clazz.getAnnotation(Scope.class);
        if(scope.value().equals("prototype")){
            return context.getBean(clazz);
        }
        throw new RuntimeException("此方法仅用于获取prototype的spring组件。");
    }

}
