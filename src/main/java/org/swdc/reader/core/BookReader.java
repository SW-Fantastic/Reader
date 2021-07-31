package org.swdc.reader.core;

import javafx.scene.Parent;
import org.swdc.reader.entity.Book;

public interface BookReader<T> {

    Parent getView();

    void renderPage();

    void goNextPage();

    void goPreviousPage();

    <T> void goTo(T location);

    void close();

    Book getBook();

    String getLocation();

    String getChapterName();

}
