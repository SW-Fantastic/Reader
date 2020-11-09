package org.swdc.reader.services.index;

import org.swdc.reader.entity.Book;
import org.swdc.reader.repository.BookRepository;
import org.swdc.reader.services.BookIndexService;

import java.util.List;

public class PublisherIndexer extends AbstractIndexer {

    @Override
    public String name() {
        return i18n("lang@dialog-publisher");
    }

    @Override
    public String keyFieldName() {
        return "publisher";
    }

    @Override
    public List<String> getKeyWords() {
        BookRepository repository = findComponent(BookRepository.class);
        return repository.getPublishers();
    }

    @Override
    public List<Book> search(String keyWord) {
        BookIndexService indexService = findComponent(BookIndexService.class);
        return indexService.searchByEquals("publisher",keyWord);
    }
}
