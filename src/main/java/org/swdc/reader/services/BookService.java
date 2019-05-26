package org.swdc.reader.services;

import net.sf.jmimemagic.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.repository.BookRepository;
import org.swdc.reader.repository.BookTypeRepository;
import org.swdc.reader.utils.DataUtil;

import java.io.File;
import java.util.List;

/**
 * Created by lenovo on 2019/5/22.
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookTypeRepository typeRepository;

    /**
     * 更新书籍数据，和文件夹的内容同步
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncBookFolder() throws Exception {
        File file = new File("./data/library");
        File[] books = file.listFiles();
        for (File bookFile: books) {

            String sha = DataUtil.getFileShaCode(bookFile);
            Long count = bookRepository.countByShaCode(sha);
            if (count > 0) {
                continue;
            }
            Book book = new Book();
            book.setName(bookFile.getName());
            book.setTitle(bookFile.getName().split("[.]")[0]);
            book.setShaCode(sha);
            book.setSize(DataUtil.getPrintSize(bookFile.length()));

            BookType defaultType = typeRepository.getDefault();
            if (defaultType == null) {
                defaultType = new BookType();
                defaultType.setName("未分类");
                defaultType = typeRepository.save(defaultType);
            }
            try {
                book.setType(defaultType);
                MagicMatch magicMatch = Magic.getMagicMatch(bookFile,true,false);
                book.setMimeData(magicMatch.getMimeType());
                bookRepository.save(book);
            }catch (Exception e) {
             e.printStackTrace();
            }
        }
    }

    public List<BookType> listTypes() {
        return typeRepository.findAll();
    }

    public BookType getDefaultType() {
        return typeRepository.getDefault();
    }

    public boolean isTypeExist(String name) {
        BookType bookTypes = typeRepository.findByName(name);
        return !(bookTypes == null);
    }

    @Transactional
    public List<Book> getBooks(BookType type) {
        BookType bookType = typeRepository.getOne(type.getId());
        return bookType.getBooks();
    }

    public BookType createType(String name) {
        if (isTypeExist(name)) {
            return typeRepository.findByName(name);
        }
        BookType type = new BookType();
        type.setName(name);
        type = typeRepository.save(type);
        return type;
    }

    public void modifyBook(Book modifiedBook) {
        bookRepository.save(modifiedBook);
    }

}
