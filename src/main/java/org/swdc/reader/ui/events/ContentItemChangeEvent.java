package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;
import org.swdc.reader.entity.Book;

/**
 * 更换文档的时候通知目录对话框刷新内容
 */
public class ContentItemChangeEvent extends AppEvent<Book> {

    public ContentItemChangeEvent(Book source, AppComponent component) {
        super(source,component);
    }
}
