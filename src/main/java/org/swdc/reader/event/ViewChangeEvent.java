package org.swdc.reader.event;

import org.springframework.context.ApplicationEvent;

/**
 * Created by lenovo on 2019/5/19.
 */
public class ViewChangeEvent extends ApplicationEvent {

    public ViewChangeEvent(String targetView) {
        super(targetView);
    }

    @Override
    public String getSource() {
        return super.getSource().toString();
    }
}
