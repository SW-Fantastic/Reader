package org.swdc.reader.core;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.swdc.reader.entity.Book;

public interface BookReader<T> {

    Node getView();

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
