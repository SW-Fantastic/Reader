package org.swdc.reader.events;

import org.swdc.dependency.event.AbstractEvent;

public class BookWillRemoveEvent extends AbstractEvent {
    public BookWillRemoveEvent(Object message) {
        super(message);
    }
}
