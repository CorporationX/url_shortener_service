package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.URL;
import org.springframework.stereotype.Repository;

@Repository
public interface URLCacheRepository {

    void save(URL url);
}
