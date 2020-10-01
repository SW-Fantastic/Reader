package org.swdc.reader.core.event;

import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;

public class ContentsModeChangeEvent extends AppEvent<Void> {
    
    public ContentsModeChangeEvent(AppComponent source) {
        super(null, source);
    }
}
