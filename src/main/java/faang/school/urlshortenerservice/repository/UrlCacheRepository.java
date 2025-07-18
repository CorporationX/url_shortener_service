package faang.school.urlshortenerservice.repository;

import java.time.Duration;

public interface UrlCacheRepository {
    void save(String hash, String url, Duration ttl);
}
