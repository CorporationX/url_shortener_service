package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCacheRepository {
    void save(Url url);
}