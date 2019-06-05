package org.swdc.reader.ui;

import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import javafx.scene.text.Font;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by lenovo on 2019/5/19.
 */
@Configuration
public class CommonComponents {

    @Getter
    private static Map<String, Font> fontMap;

    static {
        fontMap = new LinkedHashMap<>();
        File fonts = new File("configs/fonts");
        if (!fonts.exists()) {
            fonts.mkdir();
        }
        try {
            for(File fontFile : fonts.listFiles()){
                Font font = Font.loadFont(fontFile.toURI().toURL().toExternalForm(), 18);
                fontMap.put(fontFile.getName(), font);
            }
        } catch (Exception ex) {

        }
    }


    @Bean
    public  CodepageDetectorProxy  encodeDescriptor() {
        CodepageDetectorProxy codepageDetectorProxy = CodepageDetectorProxy.getInstance();
        codepageDetectorProxy.add(JChardetFacade.getInstance());
        return codepageDetectorProxy;
    }


}
