package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@CacheConfig(cacheNames = "urls")
public class UrlCacheRepository {

    @CachePut(key = "#url.hash")
    public Url cacheUrl(Url url) {
        return url;
    }

    @Cacheable(key = "#hash")
    public Url getUrlByHash(String hash) {
        log.warn("Not found cache with hash {}", hash);
        return null;
    }
}
