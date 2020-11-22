package org.swdc.reader.ui.events;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;

public class RSSRefreshEvent extends AppEvent<Void> {

    public RSSRefreshEvent(AppComponent source) {
        super(null, source);
    }

}
