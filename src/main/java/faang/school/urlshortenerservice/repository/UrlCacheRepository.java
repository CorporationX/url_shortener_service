package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlCacheRepository {
    void save(Url url);
    Optional<String> findLongUrlByHash(String hash);
    void delete(String hash);
}
