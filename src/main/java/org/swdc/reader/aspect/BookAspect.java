package org.swdc.reader.aspect;

import org.swdc.fx.aop.Advisor;
import org.swdc.fx.aop.ExecutablePoint;
import org.swdc.fx.aop.anno.AfterReturning;
import org.swdc.fx.aop.anno.Around;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.ui.events.BooksRefreshEvent;
import org.swdc.reader.ui.events.ContentItemChangeEvent;
import org.swdc.reader.ui.events.TypeRefreshEvent;

/**
 * 发送界面的列表刷新事件的aspect。
 *
 * an aspect send refresh event to views
 */

public class BookAspect extends Advisor {


    @Around(pattern = "org.swdc.reader.services.BookService.create[\\S]+")
    public Object onCreate(ExecutablePoint point) {
        try {
            Object result = point.process();
            this.publishRefreshEvent(point.getOriginal().getName(), getBook(point));
            return result;
        } catch (Exception exc) {
            logger.error("fail to execute creation method",exc);
        }
        return null;
    }

    @Around(pattern = "org.swdc.reader.services.BookService.modify*[\\S]+")
    public Object onModify(ExecutablePoint point) {
        try {
            this.publishRefreshEvent(point.getOriginal().getName(), getBook(point));
            return point.process();
        } catch (Exception exc) {
            logger.error("fail ot execute modify method",exc);
        }
        return null;
    }

    @Around(pattern = "org.swdc.reader.services.BookService.delete[\\S]+")
    public Object onDelete(ExecutablePoint point) {
        try {
            this.publishRefreshEvent(point.getOriginal().getName(), getBook(point));
            return point.process();
        } catch (Throwable throwable) {
            logger.error("fail to execute delete method",throwable);
        }
        return null;
    }

    @AfterReturning(pattern = "org.swdc.reader.services.BookServices.sync[\\S]+")
    public void onSync() {
        try {
           this.emit(new BooksRefreshEvent(this));
        } catch (Throwable throwable) {
            logger.error("fail to execute sync method",throwable);
        }
    }

    private void publishRefreshEvent(String name, Book book) {
        if (name.toLowerCase().contains("type")) {
            this.emit(new TypeRefreshEvent(this));
        } else if (name.toLowerCase().contains("book")){
            this.emit(new BooksRefreshEvent(this));
        } else if (name.toLowerCase().contains("contentitems")) {
            this.emit(new ContentItemChangeEvent(book,this));
        }
    }

    private Book getBook(ExecutablePoint point) {
        Book book = null;
        for (Object arg: point.getParams()) {
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
