package org.swdc.reader.events;

import org.swdc.dependency.event.AbstractEvent;

public class ConfigChangedEvent extends AbstractEvent {
    public ConfigChangedEvent(Object message) {
        super(message);
    }
}
