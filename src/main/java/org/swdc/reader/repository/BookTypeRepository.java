package org.swdc.reader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.reader.entity.BookType;

import java.util.List;

/**
 * Created by lenovo on 2019/5/23.
 */
@Repository
public interface BookTypeRepository extends JpaRepository<BookType, Long> {

    @Query("FROM BookType WHERE name = '未分类'")
    BookType getDefault();

    @Query("FROM BookType WHERE name = :name")
    BookType findByName (@Param("name") String name);

}
