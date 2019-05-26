package org.swdc.reader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swdc.reader.entity.Book;

/**
 * Created by lenovo on 2019/5/22.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Long countByShaCode(@Param("shaCode")String shaCode);

    Book findByShaCode(@Param("shaCode")String shaCode);

}
