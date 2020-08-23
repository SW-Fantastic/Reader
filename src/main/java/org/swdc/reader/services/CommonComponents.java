package org.swdc.reader.services;


import info.monitorenter.cpdetector.io.*;
import javafx.scene.text.Font;
import org.swdc.fx.services.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lenovo on 2019/5/19.
 */
public class CommonComponents extends Service {

    private Map<String,Font> fontMap = new HashMap<>();

    private ThreadPoolExecutor executor;

    private CodepageDetectorProxy codepageDetectorProxy;

    @Override
    public void initialize() {
        try {
            File fontFolder = new File(getAssetsPath() + "/fonts");
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
            executor = new ThreadPoolExecutor(4,4,30, TimeUnit.MINUTES,new LinkedBlockingQueue<>());
        } catch (Exception e) {
            logger.error("fail to resolve fonts");
        }
    }

    public CodepageDetectorProxy getCodepageDetectorProxy() {
        return codepageDetectorProxy;
    }

    @Override
    public void destroy() {
        executor.shutdown();
    }

    public void submitTask(Runnable runnable) {
        this.executor.submit(runnable);
    }

    public String getFontFamily(String fileName) {
        return fontMap.get(fileName).getFamily();
    }

}
