package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;

/**
 * 刷新列表
 */
public class BooksRefreshEvent extends ApplicationEvent {

    public BooksRefreshEvent() {
        super("");
    }

}
