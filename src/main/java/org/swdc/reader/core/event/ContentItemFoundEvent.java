package org.swdc.reader.core.event;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;
import org.swdc.reader.entity.ContentsItem;

/**
 * Created by lenovo on 2019/6/5.
 */
public class ContentItemFoundEvent extends AppEvent<ContentsItem> {

    public ContentItemFoundEvent(ContentsItem item, AppComponent source) {
        super(item,source);
    }

}
