package org.swdc.reader.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/***
 *  BookRepository。
 *
 *  书籍的数据库连接对象，直接操作数据库执行SQL和获取数据。
 *  SQL中尽量使用Id而不是直接传入实体，传入Entity实体的话，
 *  将会导致一个异常。
 */
@Repository
public interface BookRepository extends JPARepository<Book, Long> {

    /**
     * 按照HashCode 查找Book，主要是防止同一个Book导入多次。
     * @param shaCode 对书籍文档文件sha256计算的结果。
     * @see org.swdc.reader.services.BookServices#sha256(Path)
     * @return 如果书籍文档已经存在，则直接返回，否则返回null。
     */
    @SQLQuery("FROM Book WHERE shaCode = :shaCode")
    Book findWithShaCode(@Param("shaCode") String shaCode);

    /**
     * 查找指定作者的书籍记录。
     *
     * @param author 作者名称
     * @return 此作者的所有书籍记录。
     */
    @SQLQuery("FROM Book WHERE author = :author")
    List<Book> getByAuthors(@Param("author") String author);

    /**
     * 获取数据库内的所有书籍作者。
     * @return 作者列表
     */
    @SQLQuery("SELECT DISTINCT author FROM Book")
    List<String> getAuthors();

    /**
     * 查找标记有指定Tag的Book记录。
     * @param tagId tag的Id
     * @return Book的记录列表
     */
    @SQLQuery("FROM Book as tableBook INNER JOIN tableBook.tags as tableTag where tableTag.id = :tag")
    List<Book> getByTagContains(@Param("tag")Long tagId);

    /**
     * 查找所有出版商名称
     * @return 出版商列表
     */
    @SQLQuery("SELECT DISTINCT publisher FROM Book")
    List<String> getPublishers();


    @SQLQuery("FROM Book WHERE publisher = :publisher")
    List<Book> getByPublisher(@Param("publisher") String publisher);

    @SQLQuery(value = "FROM Book WHERE title like :prefix")
    List<Book> getByTitleContains(@Param(value = "prefix",searchBy = true)String prefix);

}
