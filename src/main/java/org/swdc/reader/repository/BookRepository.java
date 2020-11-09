package org.swdc.reader.repository;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.reader.entity.Book;

import java.util.List;
import java.util.Set;

/**
 * Created by lenovo on 2019/5/22.
 */
public interface BookRepository extends JPARepository<Book, Long> {

    @SQLQuery("SELECT count(1) FROM Book WHERE shaCode = :shaCode")
    Long countByShaCode(@Param("shaCode") String shaCode);

    @SQLQuery("FROM Book WHERE name LIKE :name")
    Set<Book> findByNameContaining(@Param(value = "name", searchBy = true) String name);

    @SQLQuery("SELECT DISTINCT author FROM Book")
    List<String> getAuthors();

    @SQLQuery("SELECT DISTINCT publisher FROM Book")
    List<String> getPublishers();

}
