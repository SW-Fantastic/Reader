package org.swdc.reader.services;

import jakarta.inject.Inject;
import org.swdc.data.anno.Transactional;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.repo.BookRepository;
import org.swdc.reader.repo.BookTagRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookTagServices {

    @Inject
    private BookTagRepository repository;

    @Inject
    private BookRepository bookRepository;

    @Transactional
    public List<BookTag> getTags() {
        List<BookTag> tags = repository.getAll();
        if (tags == null) {
            return new ArrayList<>();
        } else {
            return tags.stream()
                    .filter(t -> t.getName() != null && !t.getName().isEmpty())
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public BookTag getTag(String name) {
        try {
            return repository.findByName(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void addBook(String tag,Book book) {
        BookTag target = this.getTag(tag);
        if (target == null) {
            return;
        }
        target.getBooks().add(book);
        repository.save(target);
    }

    @Transactional
    public Set<BookTag> getBookTags(Book book) {
        Book target = bookRepository.getOne(book.getId());
        if (target == null) {
            return new HashSet<>();
        } else {
            return target.getTags().stream()
                    .filter( t -> t.getName() != null && !t.getName().isEmpty())
                    .collect(Collectors.toSet());
        }
    }

    @Transactional
    public Book clearBookTags(Book book) {
        book = bookRepository.getOne(book.getId());
        Set<BookTag> tags = book.getTags();
        for (BookTag tag: tags) {
            tag.getBooks().remove(book);
            tag = repository.save(tag);
            if (tag.getBooks().size() == 0) {
                repository.remove(tag);
            }
        }
        return book;
    }

    @Transactional
    public void removeTag(Book book, BookTag tag) {
        book = bookRepository.getOne(book.getId());
        tag = repository.getOne(tag.getId());
        if (book == null || tag == null) {
            return;
        }
        Book[] books = tag.getBooks().toArray(new Book[0]);
        for (int idx = 0; idx < books.length; idx ++) {
            Book b = books[idx];
            if (b.getId().equals(book.getId())) {
                tag.getBooks().remove(b);
            }
        }
        tag.getBooks().remove(book);
        this.repository.save(tag);

        if (tag.getBooks().size() == 0) {
            repository.remove(tag);
        }
    }


    @Transactional
    public BookTag create(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            BookTag tag = this.repository.findByName(name);
            if (tag == null) {
                throw new NullPointerException();
            }
            return tag;
        } catch (Exception e) {
            BookTag tag = new BookTag();
            tag.setName(name);
            tag.setBooks(new HashSet<>());
            return repository.save(tag);
        }
    }

}
