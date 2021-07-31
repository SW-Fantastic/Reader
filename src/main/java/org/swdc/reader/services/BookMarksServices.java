package org.swdc.reader.services;

import jakarta.inject.Inject;
import org.swdc.data.anno.Transactional;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.repo.BookMarksRepository;
import org.swdc.reader.repo.BookRepository;

import java.util.List;
import java.util.stream.Collectors;

public class BookMarksServices {

    @Inject
    private BookRepository bookRepository;

    @Inject
    private BookMarksRepository repository;

    @Transactional
    public BookMark create(Book book, String location, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        book = bookRepository.getOne(book.getId());
        BookMark bookMark = new BookMark();
        bookMark.setMarkFor(book);
        bookMark.setLocation(location);
        bookMark.setDescription(name);
        return repository.save(bookMark);
    }

    @Transactional
    public List<BookMark> getMarks(Book book) {
        return this.repository.findByBook(book.getId())
                .stream()
                .filter(m -> m.getDescription() != null && !m.getDescription().isEmpty())
                .collect(Collectors.toList());
    }

}
