package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/6/7.
 */
public class MarkItemChangeEvent extends ApplicationEvent {

    public MarkItemChangeEvent(Book source) {
        super(source);
    }

    @Override
    public Book getSource() {
        return (Book) super.getSource();
    }
}
