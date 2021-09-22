package org.swdc.reader.services;

import jakarta.inject.Inject;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.codec.digest.DigestUtils;
import org.swdc.data.anno.Transactional;
import org.swdc.fx.FXResources;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;
import org.swdc.reader.repo.BookRepository;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理Book的记录。
 */
public class BookServices {

    @Inject
    private FXResources resources;

    @Inject
    private BookRepository bookRepository;

    @Inject
    private BookTagServices tagServices;

    @Transactional
    public Book createBook(Book book) {
        if (book.getId() != null) {
            return null;
        }

        Path file = resources.getAssetsFolder()
                .toPath()
                .resolve("library")
                .resolve(book.getName());

        if (!Files.exists(file)) {
            throw new RuntimeException("找不到数据文件：" + file.toAbsolutePath().toString());
        }

        String shaCode = this.sha256(file);
        Book exist = bookRepository.findWithShaCode(shaCode);
        if (exist != null) {
            return exist;
        }
        book.setShaCode(shaCode);
        book.setSize(this.getSize(file.toFile().length()));

        try {
            MagicMatch magicMatch = Magic.getMagicMatch(file.toFile(),true,false);
            book.setMimeData(magicMatch.getMimeType());
        } catch (Exception e) {
            book.setMimeData("unknown");
        }

        return bookRepository.save(book);
    }

    @Transactional
    public List<Book> searchByName(String prefix) {
        return bookRepository.getByTitleContains(prefix);
    }

    public String sha256(Path file) {
        try {
            byte[] data = Files.readAllBytes(file);
            return DigestUtils.sha256Hex(data);
        } catch (Exception e) {
            throw new RuntimeException("sha计算失败");
        }
    }

    @Transactional
    public boolean isBookFileExist(File bookFile) {
        String sha = this.sha256(bookFile.toPath());
        Book book = this.bookRepository.findWithShaCode(sha);
        return book != null;
    }

    @Transactional
    public List<String> getAuthors() {
        List<String> authors = bookRepository.getAuthors();
        if (authors == null) {
            return new ArrayList<>();
        } else {
            return authors.stream().filter(a -> a != null && !a.isEmpty())
                    .collect(Collectors.toList());
        }

    }

    @Transactional
    public List<Book> getByAuthor(String author) {
        return bookRepository.getByAuthors(author);
    }

    @Transactional
    public List<Book> getByPublisher(String publisher) {
        return this.bookRepository.getByPublisher(publisher);
    }

    @Transactional
    public List<Book> findByTag(Long tagId) {
        return bookRepository.getByTagContains(tagId);
    }

    @Transactional
    public List<String> getPublishers() {
        List<String> publisher = this.bookRepository.getPublishers();
        if (publisher == null) {
            return new ArrayList<>();
        } else {
            return publisher
                    .stream()
                    .filter(p -> p!= null && !p.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public Book save(Book book) {
        if (book.getId() == null) {
            return this.createBook(book);
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void remove(Long id) {
        Book book = bookRepository.getOne(id);
        if (book == null) {
            return;
        }
        Set<BookTag> tags = tagServices.getBookTags(book);
        for (BookTag tag: tags) {
            tag.getBooks().remove(book);
        }
        book = tagServices.clearBookTags(book);
        bookRepository.remove(book);
        File file = this.getFile(book);
        if (file.exists()) {
            if (!file.delete()) {
                file.deleteOnExit();
            }
        }
    }

    public File getFile(Book book) {
        Path file = resources.getAssetsFolder()
                .toPath()
                .resolve("library")
                .resolve(book.getName());
        return file.toFile();
    }

    public String getSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double value = (double) size;
        if (value < 1024) {
            return value + "B";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (value < 1024) {
            return value + "KB";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        if (value < 1024) {
            return value + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return value + "GB";
        }
    }


}
