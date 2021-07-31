package org.swdc.reader.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookTag;

import java.util.List;

@Repository
public interface BookTagRepository extends JPARepository<BookTag, Long> {

    @SQLQuery("FROM BookTag WHERE name = :name")
    BookTag findByName(@Param("name") String name);


}
