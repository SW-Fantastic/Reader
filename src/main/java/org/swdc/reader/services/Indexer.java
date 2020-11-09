package org.swdc.reader.services;

import org.swdc.reader.entity.Book;

import java.util.List;

public interface Indexer {

    String name();

    String keyFieldName();

    List<String> getKeyWords();

    List<Book> search(String keyWord);

}
