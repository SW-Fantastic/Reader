package org.swdc.reader.repository;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;

/**
 * Created by lenovo on 2019/5/27.
 */
public interface MarksRepository extends JPARepository<BookMark, Long> {

    @SQLQuery("FROM BookMark WHERE markFor = :markFor AND location = :location")
    BookMark findByMarkForAndLocation(@Param("markFor") Book book, @Param("location") String location);

}
