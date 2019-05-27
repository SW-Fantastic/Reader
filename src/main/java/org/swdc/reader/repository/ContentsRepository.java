package org.swdc.reader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.swdc.reader.entity.ContentsItem;

/**
 * Created by lenovo on 2019/5/27.
 */
@Repository
public interface ContentsRepository extends JpaRepository<ContentsItem, Long> {
}
