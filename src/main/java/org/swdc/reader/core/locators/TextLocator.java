package org.swdc.reader.core.locators;

import org.swdc.reader.core.BookLocator;
import org.swdc.reader.entity.Book;

import java.io.File;
import java.util.HashMap;

/**
 * Created by lenovo on 2019/5/30.
 */
public class TextLocator implements BookLocator<String> {

    private File bookFile;
    private Book bookEntity;

    private HashMap<Integer, String> locationTextMap;
    private Integer currentPage = 0;

    public TextLocator(Book book) {

    }

    @Override
    public String prevPage() {
        return null;
    }

    @Override
    public String nextPage() {
        return null;
    }

    @Override
    public String toPage(String location) {
        return null;
    }

    @Override
    public String currentPage() {
        return null;
    }

}
