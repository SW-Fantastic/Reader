package org.swdc.reader.core;

import javafx.scene.layout.BorderPane;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/5/28.
 */
public interface BookReader<T> {

    void setBook(Book book);

    void renderPage(T pageData, BorderPane view);

    boolean isSupport(Book target);

    BookLocator<T> getLocator();

}
