package org.swdc.reader.core;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.swdc.reader.entity.Book;

public interface BookReader<T> {

    BorderPane getView();

    void renderPage();

    void goNextPage();

    void goPreviousPage();

    <T> void goTo(T location);

    void close();

    Book getBook();

    String getLocation();

    String getChapterName();

    default boolean viewScrollable() {
        return false;
    }

}
