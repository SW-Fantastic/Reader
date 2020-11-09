package org.swdc.reader.services.index;

import org.swdc.fx.container.DefaultContainer;

public class IndexerManager extends DefaultContainer<AbstractIndexer> {

    @Override
    public boolean isComponentOf(Class clazz) {
        return AbstractIndexer.class.isAssignableFrom(clazz);
    }

}
