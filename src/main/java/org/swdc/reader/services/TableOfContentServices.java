package org.swdc.reader.services;

import jakarta.inject.Inject;
import nl.siegmann.epublib.domain.TableOfContents;
import org.swdc.data.anno.Transactional;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.repo.BookRepository;
import org.swdc.reader.repo.TableOfContentsRepository;

import java.util.List;

public class TableOfContentServices {

    @Inject
    private TableOfContentsRepository repository;

    @Inject
    private BookRepository bookRepository;

    @Transactional
    public ContentsItem create(String title, String location, Book book) {

        try {
            ContentsItem item = repository.getContentItemByLocation(location);
            if (item == null) {
                throw new NullPointerException();
            }
            return item;
        } catch (NullPointerException e) {
            ContentsItem item = new ContentsItem();
            item.setTitle(title);
            item.setLocated(book);
            item.setLocation(location);
            return repository.save(item);
        }
    }

    @Transactional
    public boolean hasTableOfContents(Book book) {
        if (book == null) {
            return false;
        }
        book = this.bookRepository.getOne(book.getId());
        List<ContentsItem> toc = this.getTableOfContent(book);
        return toc != null && toc.size() > 0;
    }

    @Transactional
    public List<ContentsItem> getTableOfContent(Book book) {
        book = bookRepository.getOne(book.getId());
        return this.repository.getContentItemsOfBook(book.getId());
    }

}
