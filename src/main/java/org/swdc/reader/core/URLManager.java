package org.swdc.reader.core;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URLManager implements URLStreamHandlerFactory {

    public interface URLHandler {
        URLStreamHandler accept();
    }

    private static String PREFIX = "sun.net.www.protocol";

    private static URLManager manager = new URLManager();

    private static Map<String,URLHandler> handlers = new ConcurrentHashMap<>();

    public static void register(String name,URLHandler handler) {
        handlers.put(name,handler);
    }

    public static void setup() {
        try {
            URL.setURLStreamHandlerFactory(getManager());
        } catch (Exception e) {
        }
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        URLStreamHandler handler = createDefault(protocol);
        if (handler != null) {
            return handler;
        }
        return handlers.get(protocol).accept();
    }


    public static URLStreamHandler createDefault(String protocol) {
        String name = PREFIX + "." + protocol + ".Handler";
        try {
            @SuppressWarnings("deprecation")
            Object o = Class.forName(name).newInstance();
            return (URLStreamHandler)o;
        } catch (ClassNotFoundException x) {
            // ignore
        } catch (Exception e) {
            // For compatibility, all Exceptions are ignored.
            // any number of exceptions can get thrown here
        }
        return null;
    }

    public static URLManager getManager() {
        return manager;
    }
}
