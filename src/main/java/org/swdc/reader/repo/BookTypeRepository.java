package org.swdc.reader.repo;

import org.swdc.data.JPARepository;
import org.swdc.data.anno.Param;
import org.swdc.data.anno.Repository;
import org.swdc.data.anno.SQLQuery;
import org.swdc.reader.entity.BookType;

/**
 * Created by lenovo on 2019/5/23.
 */
@Repository
public interface BookTypeRepository extends JPARepository<BookType, Long> {

    @SQLQuery("FROM BookType WHERE name = '未分类'")
    BookType getDefault();

    @SQLQuery("FROM BookType WHERE name = :name")
    BookType findByName (@Param("name") String name);

}
