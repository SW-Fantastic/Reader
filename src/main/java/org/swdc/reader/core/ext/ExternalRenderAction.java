package org.swdc.reader.core.ext;

@FunctionalInterface
public interface ExternalRenderAction<T> {

    T process(T data);

}
