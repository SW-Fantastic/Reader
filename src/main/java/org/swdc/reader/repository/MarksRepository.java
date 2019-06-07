package org.swdc.reader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.BookMark;

/**
 * Created by lenovo on 2019/5/27.
 */
@Repository
public interface MarksRepository extends JpaRepository<BookMark, Long> {

    BookMark findByMarkForAndLocation(@Param("markFor")Book book, @Param("location")String location);

}
