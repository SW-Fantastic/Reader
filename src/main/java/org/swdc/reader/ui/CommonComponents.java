package org.swdc.reader.ui;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lenovo on 2019/5/19.
 */
@Configuration
public class CommonComponents {

    @Bean
    public  CodepageDetectorProxy  encodeDescriptor() {
        CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance();
        codepageDetectorProxy.add(JChardetFacade.getInstance());
        return codepageDetectorProxy;
    }


}
