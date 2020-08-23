package org.swdc.reader.core.event;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;

/**
 * 改变书籍的当前的位置
 */
public class BookLocationEvent extends AppEvent<String> {

    public BookLocationEvent(String location, AppComponent component) {
        super(location,component);
    }

}
