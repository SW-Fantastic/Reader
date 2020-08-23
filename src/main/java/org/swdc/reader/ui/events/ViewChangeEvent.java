package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;

/**
 * Created by lenovo on 2019/5/19.
 */
public class ViewChangeEvent extends AppEvent {

    public ViewChangeEvent(String name, AppComponent source) {
        super(name,source);
    }

    public String getName() {
        return getData().toString();
    }
}
