package faang.school.urlshortenerservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class UrlCacheRepository {

    @CachePut(value = "url", key = "#hash")
    public String saveToCache(String hash, String originalUrl) {
        log.info("Saving association to the cache: {}, {}", hash, originalUrl);
        return originalUrl;
    }
}
