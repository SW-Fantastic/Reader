package org.swdc.reader.services;

import jakarta.inject.Inject;
import org.swdc.data.anno.Transactional;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.repo.BookTypeRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeServices {

    @Inject
    private BookTypeRepository typeRepository;

    @Inject
    private BookServices bookServices;

    @Transactional
    public List<BookType> allTypes() {
        return typeRepository.getAll();
    }

    @Transactional
    public BookType create(String name) {
        BookType type = this.typeRepository.findByName(name);
        if (type != null){
            return type;
        }
        type = new BookType();
        type.setName(name);
        return typeRepository.save(type);
    }

    @Transactional
    public void remove(Long id) {
        BookType type = this.typeRepository.getOne(id);
        if (type == null) {
            return;
        }
        Set<Book> books = type.getBooks();
        for (Book book: books) {
            bookServices.remove(book.getId());
        }
        typeRepository.remove(type);
    }

    @Transactional
    public BookType rename(Long typeId, String typeName)  {
        BookType target = typeRepository.getOne(typeId);
        if (target == null) {
            return null;
        }
        target.setName(typeName);
        return typeRepository.save(target);
    }


    @Transactional
    public BookType findTypeById(Long id) {
        return typeRepository.getOne(id);
    }


}
