package faang.school.urlshortenerservice.repository;

import java.util.Optional;

public interface UrlCacheRepository {
    void save(String hash, String url);
    Optional<String> find(String hash);
}
