package org.swdc.reader.aspects;

import lombok.extern.apachecommons.CommonsLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.swdc.reader.event.BooksRefreshEvent;
import org.swdc.reader.event.TypeRefreshEvent;

/**
 * Created by lenovo on 2019/5/25.
 */

@Aspect
@Component
@CommonsLog
public class BookAspect {

    @Autowired
    private ApplicationContext context;

    @Around("execution(* org.swdc.reader.services.BookService.create*(..))")
    public Object onCreate(ProceedingJoinPoint point) {
        try {
            this.publishRefreshEvent(point.getSignature().getName());
            return point.proceed(point.getArgs());
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return null;
    }

    @Around("execution(* org.swdc.reader.services.BookService.modify*(..))")
    protected Object onModify(ProceedingJoinPoint point) {
        try {
            this.publishRefreshEvent(point.getSignature().getName());
            return point.proceed(point.getArgs());
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return null;
    }

    private void publishRefreshEvent(String name) {
        if (name.toLowerCase().contains("type")) {
            context.publishEvent(new TypeRefreshEvent());
        } else if (name.toLowerCase().contains("book")){
            context.publishEvent(new BooksRefreshEvent());
        }
    }

}
