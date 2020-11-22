package org.swdc.reader.aspect;

import org.swdc.fx.aop.Advisor;
import org.swdc.fx.aop.ExecutablePoint;
import org.swdc.fx.aop.anno.AfterReturning;
import org.swdc.fx.aop.anno.Around;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.entity.RSSSource;
import org.swdc.reader.ui.events.BooksRefreshEvent;
import org.swdc.reader.ui.events.ContentItemChangeEvent;
import org.swdc.reader.ui.events.RSSRefreshEvent;
import org.swdc.reader.ui.events.TypeRefreshEvent;

/**
 * 发送界面的列表刷新事件的aspect。
 *
 * an aspect send refresh event to views
 */

public class BookAspect extends Advisor {


    @Around(pattern = "org.swdc.reader.services.[\\S]+Service.create[\\S]+")
    public Object onCreate(ExecutablePoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(result,point);
            return result;
        } catch (Exception exc) {
            logger.error("fail to execute creation method",exc);
        }
        return null;
    }

    @Around(pattern = "org.swdc.reader.services.[\\S]+Service.modify[\\S]+")
    public Object onModify(ExecutablePoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(result,point);
            return result;
        } catch (Exception exc) {
            logger.error("fail ot execute modify method",exc);
        }
        return null;
    }

    @Around(pattern = "org.swdc.reader.services.[\\S]+Service.delete[\\S]+")
    public Object onDelete(ExecutablePoint point) {
        try {
            Object result = point.process();
            this.dispatchRefreshEvents(result,point);
            return result;
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

    private void dispatchRefreshEvents(Object result,ExecutablePoint point) {
        Book book = getParam(point,Book.class);
        if (book != null) {
            this.emit(new BooksRefreshEvent(this));
            return;
        }

        ContentsItem contentsItem = getParam(point,ContentsItem.class);
        if (contentsItem != null) {
            this.emit(new ContentItemChangeEvent(contentsItem.getLocated(),this));
            return;
        }

        BookType type = getParam(point,BookType.class);
        if (type != null) {
            this.emit(new TypeRefreshEvent(this));
            return;
        }

        RSSSource rssSource = getParam(point,RSSSource.class);
        if (rssSource != null) {
            this.emit(new RSSRefreshEvent(this));
        }
    }

    private <T> T getParam(ExecutablePoint point, Class<T> paramType) {
        T result = null;
        for (Object arg: point.getParams()) {
            if (arg.getClass() == paramType) {
                result = (T) arg;
            }
        }
        return result;
    }

}
