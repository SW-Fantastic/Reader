package org.swdc.reader.services.index;

import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.repository.BookTagRepository;
import org.swdc.reader.services.BookIndexService;

import java.util.List;
import java.util.stream.Collectors;

public class TagIndexer extends AbstractIndexer {
    @Override
    public String name() {
        return i18n("lang@dialog-tag");
    }

    @Override
    public String keyFieldName() {
        return "tags";
    }

    @Override
    public List<String> getKeyWords() {
        BookTagRepository tagRepository = findComponent(BookTagRepository.class);
        return tagRepository.getAll()
                .stream()
                .map(BookTag::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> search(String keyWord) {
        BookIndexService indexService = findComponent(BookIndexService.class);
        return indexService.searchByWildcard(this.keyFieldName(),keyWord);
    }
}
