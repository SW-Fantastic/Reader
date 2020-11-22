package org.swdc.reader.services;

import org.swdc.fx.anno.Aware;
import org.swdc.fx.jpa.anno.Transactional;
import org.swdc.fx.services.Service;
import org.swdc.reader.entity.RSSSource;
import org.swdc.reader.repository.RSSRepository;

import java.util.List;

public class RSSService extends Service {

    @Aware
    private RSSRepository repository = null;

    @Transactional
    public RSSSource createRssSource(RSSSource source) {
        if (source == null) {
            return null;
        }
        if (source.getUrl() == null || source.getUrl().isBlank()) {
            return null;
        }
        if (source.getName() == null || source.getName().isBlank()) {
            return null;
        }
        return repository.save(source);
    }

    @Transactional
    public RSSSource modifyRssSource(RSSSource source) {
        if (source == null || source.getId() == null) {
            return null;
        }
        return repository.save(source);
    }

    @Transactional
    public void deleteRssSource(RSSSource source) {
        if (source == null || source.getId() == null) {
            return;
        }
        repository.remove(source);
    }


    public RSSSource findByAddress(String address) {
        return null;
    }

    public List<RSSSource> getAllRss() {
        return repository.getAll();
    }

}
