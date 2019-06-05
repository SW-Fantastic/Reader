package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;
import org.swdc.reader.entity.ContentsItem;

/**
 * Created by lenovo on 2019/6/5.
 */
public class ContentItemFoundEvent extends ApplicationEvent {

    public ContentItemFoundEvent(ContentsItem item) {
        super(item);
    }

    @Override
    public ContentsItem getSource() {
        return (ContentsItem) super.getSource();
    }
}
