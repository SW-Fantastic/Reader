package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;

/**
 * Created by lenovo on 2019/5/25.
 */
public class TypeRefreshEvent extends AppEvent<String> {
    public TypeRefreshEvent(AppComponent component) {
        super("",component);
    }
}
