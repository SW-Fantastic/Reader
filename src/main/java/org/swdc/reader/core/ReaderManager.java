package org.swdc.reader.core;

import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.DependencyScope;
import org.swdc.fx.view.AbstractView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个Scope
 */
public class ReaderManager implements DependencyScope {

    private DependencyContext context;
    private Map<Class,List<AbstractView>> views = new ConcurrentHashMap<>();
    private Map<String, AbstractView> namedViews = new ConcurrentHashMap<>();
    private Map<Class, List<AbstractView>> abstractViews = new ConcurrentHashMap<>();

    @Override
    public Class getScopeType() {
        return Reader.class;
    }

    @Override
    public <T> T put(String s, Class clazz, Class abstractClazz, T t) {
        return null;
    }

    @Override
    public <T> T put(String s, Class clazz, T t) {
        return null;
    }

    @Override
    public void setContext(DependencyContext dependencyContext) {
        this.context = dependencyContext;
    }

    @Override
    public <T> T getByClass(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T getByName(String s) {
        return null;
    }

    @Override
    public <T> List<T> getByAbstract(Class<T> aClass) {
        return null;
    }

    @Override
    public List<Object> getAllComponent() {
        return null;
    }
}
