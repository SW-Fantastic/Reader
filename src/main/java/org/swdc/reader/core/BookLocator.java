package org.swdc.reader.core;

/**
 * Created by lenovo on 2019/5/28.
 */
public interface BookLocator<T> {

    T prevPage();

    T nextPage();

    T toPage(String location);

    T currentPage();

}
