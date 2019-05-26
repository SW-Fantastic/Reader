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
    private String theme;

    @Getter
    @Setter
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
