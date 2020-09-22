package org.swdc.reader.core.readers;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.swdc.fx.AppComponent;
import org.swdc.reader.core.BookReader;
import org.swdc.reader.core.ext.ExternalRenderAction;

public abstract class AbstractReader<T> extends AppComponent implements BookReader<T> {

    protected ObservableMap<Class, ExternalRenderAction> renderActionObservableMap = FXCollections.observableHashMap();

    @Override
    public void initialize() {
        renderActionObservableMap.addListener(this::onRendersChange);
    }

    public void addRenderAction(ExternalRenderAction action) {
        renderActionObservableMap.put(action.getClass(),action);
    }

    public <T> T removeRenderAction(Class<T> action) {
        return (T)renderActionObservableMap.remove(action);
    }

    public void clearActions() {
        renderActionObservableMap.clear();
    }

    protected abstract void reload();

    private void onRendersChange(MapChangeListener.Change<? extends Class, ? extends ExternalRenderAction> change) {
        this.reload();
    }

}
