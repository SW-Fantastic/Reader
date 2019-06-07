package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.reader.entity.BookMark;

/**
 * Created by lenovo on 2019/6/7.
 */
public class MarkCreateEvent extends ApplicationEvent {

    public MarkCreateEvent(BookMark source) {
        super(source);
    }

}
