package org.swdc.reader.services.index;

import org.swdc.reader.entity.Book;
import org.swdc.reader.repository.BookRepository;
import org.swdc.reader.services.BookIndexService;

import java.util.List;

public class AuthorIndexer extends AbstractIndexer {

    @Override
    public String name() {
        return i18n("lang@dialog-author");
    }

    @Override
    public String keyFieldName() {
        return "author";
    }

    @Override
    public List<String> getKeyWords() {
        BookRepository repository = findComponent(BookRepository.class);
        return repository.getAuthors();
    }

    @Override
    public List<Book> search(String keyWord) {
        BookIndexService indexService = findComponent(BookIndexService.class);
        return indexService.searchByEquals(this.keyFieldName(),keyWord);
    }
}
