package org.swdc.reader.repository;

import org.swdc.fx.jpa.JPARepository;
import org.swdc.fx.jpa.anno.Param;
import org.swdc.fx.jpa.anno.SQLQuery;
import org.swdc.reader.entity.RSSSource;

public interface RSSRepository extends JPARepository<RSSSource,Long> {

    @SQLQuery("FROM RSSSource WHERE url = :address")
    RSSSource findByAddress(@Param("address") String address);

}
