package org.swdc.reader;


import org.swdc.config.annotations.ConfigureSource;
import org.swdc.config.configs.PropertiesHandler;

@ConfigureSource(value = "assets/application.properties",handler = PropertiesHandler.class)
public class ApplicationConfig extends org.swdc.fx.config.ApplicationConfig {


}
