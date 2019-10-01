package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;

public class BookProcessEvent extends ApplicationEvent {

    private String message;

    public BookProcessEvent(Double source, String message) {
        super(source);
        this.message = message;
    }

    @Override
    public Double getSource() {
        return (Double) super.getSource();
    }

    public String getMessage() {
        return message;
    }
}
