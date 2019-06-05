package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.reader.entity.Book;

/**
 * 更换文档的时候通知目录对话框刷新内容
 */
public class ContentItemChangeEvent extends ApplicationEvent {

    public ContentItemChangeEvent(Book source) {
        super(source);
    }

    @Override
    public Book getSource() {
        return (Book)super.getSource();
    }
}
