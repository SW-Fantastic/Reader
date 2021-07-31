package org.swdc.reader.events;

import org.swdc.dependency.event.AbstractEvent;
import org.swdc.reader.entity.BookType;

public class TypeSelectEvent extends AbstractEvent {

    public TypeSelectEvent(BookType message) {
        super(message);
    }

}
