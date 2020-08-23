package org.swdc.reader.core.readers;

import org.swdc.fx.container.DefaultContainer;

public class ReaderManager extends DefaultContainer<AbstractReader> {
    @Override
    public boolean isComponentOf(Class aClass) {
        return AbstractReader.class.isAssignableFrom(aClass);
    }
}
