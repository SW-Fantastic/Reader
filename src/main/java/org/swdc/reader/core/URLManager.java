package org.swdc.reader.core;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.net.spi.URLStreamHandlerProvider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URLManager extends URLStreamHandlerProvider implements URLStreamHandlerFactory {

    public interface URLHandler {
        URLStreamHandler accept();
    }

    private static Map<String,URLHandler> handlers = new ConcurrentHashMap<>();

    public static void register(String name,URLHandler handler) {
        handlers.put(name,handler);
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return handlers.get(protocol).accept();
    }

}
