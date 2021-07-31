package org.swdc.reader.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;

import java.util.List;

/**
 * Created by lenovo on 2019/5/27.
 */
@Repository
public interface BookMarksRepository extends JPARepository<BookMark, Long> {

    @SQLQuery("FROM BookMark WHERE markFor.id = :bookId")
    List<BookMark> findByBook(@Param("bookId") Long bookId);

}
