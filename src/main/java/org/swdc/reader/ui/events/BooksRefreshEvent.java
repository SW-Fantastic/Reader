package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;


/**
 * 刷新列表
 */
public class BooksRefreshEvent extends AppEvent<String> {

    public BooksRefreshEvent(AppComponent component) {
        super("",component);
    }

}
