package org.swdc.reader;

import javafx.application.Application;

import java.util.concurrent.ExecutionException;

/**
 * 用于启动JavaFX
 * @author SW-Fantastic
 */
public class Launcher {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ReaderApplication application = new ReaderApplication();
        application.applicationLaunch(args);
    }

}
