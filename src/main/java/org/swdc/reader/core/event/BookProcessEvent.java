package org.swdc.reader.core.event;


import org.swdc.fx.AppComponent;
import org.swdc.fx.event.AppEvent;

public class BookProcessEvent extends AppEvent<Double> {

    private String message;

    public BookProcessEvent(Double progress, String message, AppComponent source) {
        super(progress,source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
