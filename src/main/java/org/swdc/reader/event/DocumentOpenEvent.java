package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/5/31.
 */
public class DocumentOpenEvent extends ApplicationEvent {

    public DocumentOpenEvent(Book source) {
        super(source);
    }

    @Override
    public Book getSource() {
        return (Book)super.getSource();
    }
}
