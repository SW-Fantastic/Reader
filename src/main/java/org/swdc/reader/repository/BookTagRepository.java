package org.swdc.reader.repository;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.reader.entity.BookTag;

import java.util.List;

public interface BookTagRepository extends JPARepository<BookTag, Long> {

    @SQLQuery("FROM BookTag WHERE name LIKE :name")
    List<BookTag> findByNameContaining(@Param(value = "name", searchBy = true) String name);

    @SQLQuery("FROM BookTag WHERE name = :name")
    BookTag findByName(@Param("name") String name);

}
