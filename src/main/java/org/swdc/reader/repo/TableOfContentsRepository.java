package org.swdc.reader.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;

import java.util.List;

/**
 * Created by lenovo on 2019/5/27.
 */
@Repository
public interface TableOfContentsRepository extends JPARepository<ContentsItem, Long> {

    @SQLQuery("From ContentsItem Where located.id = :bookId")
    List<ContentsItem> getContentItemsOfBook(@Param("bookId") Long bookId);

    @SQLQuery("From ContentsItem Where location = :location AND located.id = :book")
    ContentsItem getContentItemByLocation(@Param("location") String location, @Param("book")Long book);

}
