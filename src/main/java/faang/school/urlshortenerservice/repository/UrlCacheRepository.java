package faang.school.urlshortenerservice.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlCacheRepository {
    void save(String hash, String url);

    Optional<String> findOriginalUrl(String hash);
}
