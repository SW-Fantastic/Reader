package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.reader.entity.ContentsItem;

/**
 * 改变书籍的当前的位置
 */
public class BookLocationEvent extends ApplicationEvent {

    public BookLocationEvent(ContentsItem source) {
        super(source);
    }

    @Override
    public ContentsItem getSource() {
        return (ContentsItem)super.getSource();
    }
}
