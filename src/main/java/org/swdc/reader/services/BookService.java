package org.swdc.reader.services;

import lombok.extern.apachecommons.CommonsLog;
import net.sf.jmimemagic.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;
import org.swdc.reader.entity.BookType;
import org.swdc.reader.entity.ContentsItem;
import org.swdc.reader.repository.BookRepository;
import org.swdc.reader.repository.BookTypeRepository;
import org.swdc.reader.repository.ContentsRepository;
import org.swdc.reader.repository.MarksRepository;
import org.swdc.reader.ui.ApplicationConfig;
import org.swdc.reader.utils.DataUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2019/5/22.
 */
@Service
@CommonsLog
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookTypeRepository typeRepository;

    @Autowired
    private ContentsRepository contentsRepository;

    @Autowired
    private MarksRepository marksRepository;

    /**
     * 更新书籍数据，和文件夹的内容同步
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncBookFolder() throws Exception {
        File file = new File("./data/library");
        if (!file.exists()) {
            file.mkdir();
            return;
        }
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
        BookType defaultType = typeRepository.getDefault();
        if (defaultType == null) {
            this.createType("未分类");
            defaultType = typeRepository.getDefault();
        }
        return defaultType;
    }

    public boolean isTypeExist(String name) {
        BookType bookTypes = typeRepository.findByName(name);
        return !(bookTypes == null);
    }

    @Transactional
    public Set<Book> getBooks(BookType type) {
        BookType bookType = typeRepository.getOne(type.getId());
        return bookType.getBooks();
    }

    public Set<Book> searchByName(String name) {
        return bookRepository.findByNameContaining(name);
    }

    public Book fromFile(File file) {
        BookType defaultType = typeRepository.getDefault();
        if (defaultType == null) {
            defaultType = new BookType();
            defaultType.setName("未分类");
            defaultType = typeRepository.save(defaultType);
        }
        String sha = DataUtil.getFileShaCode(file);
        Book book = new Book();
        book.setTitle(file.getName().split("[.]")[0]);
        book.setName(file.getName());
        book.setShaCode(sha);
        book.setSize(DataUtil.getPrintSize(file.length()));
        try {
            book.setType(defaultType);
            MagicMatch magicMatch = Magic.getMagicMatch(file,true,false);
            book.setMimeData(magicMatch.getMimeType());
            return book;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public void createBook(Book book, File sourceFile) {
        Long count = bookRepository.countByShaCode(book.getShaCode());
        if (count > 0) {
            return;
        }
        if (book.getTitle() == null || book.getTitle().trim().equals("")) {
            return;
        }
        if (book.getName() == null || book.getName().trim().equals("")) {
            return;
        }
        if (book.getType() == null) {
            BookType defaultType = typeRepository.getDefault();
            if (defaultType == null) {
                defaultType = new BookType();
                defaultType.setName("未分类");
                defaultType = typeRepository.save(defaultType);
            }
            book.setType(defaultType);
        }
        if (book.getMimeData() == null || book.getMimeData().trim().equals("")) {
            return;
        }
        try {
            FileUtils.copyFile(sourceFile, new File("./data/library/" + sourceFile.getName()));
            bookRepository.save(book);
        } catch (IOException e) {
            log.error(e);
        }
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

    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Book target) {
        Book deleteTarget = bookRepository.getOne(target.getId());
        Set<BookMark> marks = deleteTarget.getMarks();
        Set<ContentsItem> contentsItems = deleteTarget.getContentsItems();
        if (marks != null && marks.size() > 0) {
            marksRepository.deleteAll(marks);
        }
        if (contentsItems != null && contentsItems.size() > 0) {
            contentsRepository.deleteAll(contentsItems);
        }
        File file = new File("./data/library/" + deleteTarget.getName());
        if (!file.delete()) {
            file.deleteOnExit();
        }
        bookRepository.delete(deleteTarget);
    }

    @Transactional
    public void createContentItems(ContentsItem item) {
        if (item.getLocated() == null) {
            return;
        }
        if (item.getLocation() == null || item.getLocation().trim().equals("")) {
            return;
        }
        if (item.getTitle() == null || item.getTitle().trim().equals("")) {
            return;
        }
        Book book = bookRepository.getOne(item.getLocated().getId());
        ContentsItem existed = contentsRepository.findByLocationAndLocated(item.getLocation(), book);
        if (existed == null) {
            item.setLocated(book);
            contentsRepository.save(item);
        }
    }

    public void createBookMark(BookMark mark) {
        if (mark.getLocation() == null || mark.getLocation().trim().equals("")) {
            return;
        }
        if (mark.getDescription() == null || mark.getDescription().trim().equals("")) {
            return;
        }
        Book book = bookRepository.getOne(mark.getMarkFor().getId());
        BookMark existed = marksRepository.findByMarkForAndLocation(book, mark.getLocation());
        if (existed == null) {
            mark.setMarkFor(book);
            marksRepository.save(mark);
        } else {
            existed.setDescription(mark.getDescription());
            marksRepository.save(existed);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMark(BookMark mark) {
        marksRepository.findById(mark.getId()).ifPresent(bookMark -> {
            Book book = bookRepository.getOne(bookMark.getMarkFor().getId());
            book.getMarks().remove(bookMark);
            bookRepository.save(book);
            marksRepository.delete(bookMark);
        });
    }

    public void modifyType(BookType type) {
        typeRepository.findById(type.getId()).ifPresent(bookType -> {
            if (bookType.getName().equals("未分类")) {
                return;
            }
            bookType.setName(type.getName());
            typeRepository.save(bookType);
        });
    }

    public void exportType(File targetFolder,  BookType type) {
        typeRepository.findById(type.getId()).ifPresent(bookType -> {
            try {
                Set<Book> books = type.getBooks();
                for (Book book : books) {
                    File bookFile = new File("data/library/" + book.getName());
                    File target = new File(targetFolder.getPath() + "/" + book.getName());
                    FileUtils.copyFile(bookFile, target);
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    @Transactional
    public void deleteType(BookType type) {
        typeRepository.findById(type.getId()).ifPresent(bookType -> {
            Set<Book> books = type.getBooks();
            books.forEach(this::deleteBook);
            typeRepository.delete(bookType);
        });
    }

}
