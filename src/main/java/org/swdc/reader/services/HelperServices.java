package org.swdc.reader.services;

import info.monitorenter.cpdetector.io.*;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.swdc.fx.FXResources;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class HelperServices {

    @Inject
    private FXResources resources;

    @Inject
    private Logger logger;

    private Map<String, Font> fontMap = new HashMap<>();

    private CodepageDetectorProxy codepageDetectorProxy;

    @PostConstruct
    public void initResources() {
        try {
            File fontFolder = resources.getAssetsFolder().toPath().resolve("fonts").toFile();;
            File[] contents = fontFolder.listFiles();
            if (contents != null) {
                for(File fontFile : contents){
                    Font font = Font.loadFont(new FileInputStream(fontFile), 18);
                    fontMap.put(fontFile.getName(), font);
                }
            }
            codepageDetectorProxy = CodepageDetectorProxy.getInstance();
            codepageDetectorProxy.add(new ParsingDetector(false));
            codepageDetectorProxy.add(UnicodeDetector.getInstance());
            codepageDetectorProxy.add(JChardetFacade.getInstance());
            codepageDetectorProxy.add(ASCIIDetector.getInstance());
        } catch (Exception e) {
            logger.error("fail to resolve fonts");
        }
    }

    public void submitTask(Runnable runnable) {
        this.resources.getExecutor().submit(runnable);
    }

    public <T> Future<T> submitFuture(FutureTask<T> future) {
        return (Future<T>) this.resources.getExecutor().submit(future);
    }

    public String getFontFamily(String fileName) {
        return fontMap.get(fileName).getFamily();
    }

    public File getAssetsFolder() {
        return resources.getAssetsFolder();
    }

    public CodepageDetectorProxy getCodepageDetectorProxy() {
        return codepageDetectorProxy;
    }

    public ThreadPoolExecutor getExecutor() {
        return resources.getExecutor();
    }
}
