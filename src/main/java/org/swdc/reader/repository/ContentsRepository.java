package org.swdc.reader.repository;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.reader.entity.Book;
import org.swdc.reader.entity.ContentsItem;

/**
 * Created by lenovo on 2019/5/27.
 */
public interface ContentsRepository extends JPARepository<ContentsItem, Long> {

    @SQLQuery("FROM ContentsItem WHERE location = :location AND located = :located")
    ContentsItem findByLocationAndLocated(@Param("location") String location, @Param("located") Book located);

}
