package org.swdc.reader.repository;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.reader.entity.BookType;

/**
 * Created by lenovo on 2019/5/23.
 */
public interface BookTypeRepository extends JPARepository<BookType, Long> {

    @SQLQuery("FROM BookType WHERE name = '未分类'")
    BookType getDefault();

    @SQLQuery("FROM BookType WHERE name = :name")
    BookType findByName (@Param("name") String name);

}
