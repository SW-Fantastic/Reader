package org.swdc.reader.core.ext;

import org.swdc.fx.container.DefaultContainer;

public class ExternResolverManager extends DefaultContainer<AbstractResolver> {
    @Override
    public boolean isComponentOf(Class aClass) {
        return AbstractResolver.class.isAssignableFrom(aClass);
    }
}
