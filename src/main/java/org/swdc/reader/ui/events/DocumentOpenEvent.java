package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/5/31.
 */
public class DocumentOpenEvent extends AppEvent<Book> {

    public DocumentOpenEvent(Book data, AppComponent source) {
        super(data,source);
    }

}
