package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/6/7.
 */
public class MarkItemChangeEvent extends AppEvent<Book> {

    public MarkItemChangeEvent(Book data, AppComponent source) {
        super(data,source);
    }

}
