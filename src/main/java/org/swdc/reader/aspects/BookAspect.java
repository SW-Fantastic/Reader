package org.swdc.reader.aspects;

import lombok.extern.apachecommons.CommonsLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.event.BooksRefreshEvent;
import org.swdc.reader.event.ContentItemChangeEvent;
import org.swdc.reader.event.TypeRefreshEvent;
import org.swdc.reader.services.BookService;

/**
 * 发送界面的列表刷新事件的aspect。
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
            this.publishRefreshEvent(point.getSignature().getName(), getBook(point));
            return point.proceed(point.getArgs());
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return null;
    }

    @Around("execution(* org.swdc.reader.services.BookService.modify*(..))")
    protected Object onModify(ProceedingJoinPoint point) {
        try {
            this.publishRefreshEvent(point.getSignature().getName(), getBook(point));
            return point.proceed(point.getArgs());
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return null;
    }

    @Around("execution(* org.swdc.reader.services.BookService.delete*(..))")
    protected Object onDelete(ProceedingJoinPoint point) {
        try {
            this.publishRefreshEvent(point.getSignature().getName(), getBook(point));
            return point.proceed(point.getArgs());
        } catch (Throwable throwable) {
            log.error(throwable);
        }
        return null;
    }

    @AfterReturning("execution(* org.swdc.reader.services.BookService.sync*(..))")
    protected void onSync() {
        try {
            context.publishEvent(new BooksRefreshEvent());
        } catch (Throwable throwable) {
            log.error(throwable);
        }
    }

    private void publishRefreshEvent(String name, Book book) {
        if (name.toLowerCase().contains("type")) {
            context.publishEvent(new TypeRefreshEvent());
        } else if (name.toLowerCase().contains("book")){
            context.publishEvent(new BooksRefreshEvent());
        } else if (name.toLowerCase().contains("contentitems")) {
            context.publishEvent(new ContentItemChangeEvent(book));
        }
    }

    private Book getBook(ProceedingJoinPoint point) {
        Book book = null;
        for (Object arg: point.getArgs()) {
            if (arg instanceof Book) {
                book = (Book)arg;
            }
            if (arg instanceof ContentsItem) {
                book = ((ContentsItem)arg).getLocated();
            }
        }
        return book;
    }

}
