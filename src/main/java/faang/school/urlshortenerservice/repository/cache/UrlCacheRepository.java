package faang.school.urlshortenerservice.repository.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

import static faang.school.urlshortenerservice.config.cache.CacheConfig.URL_CACHE_NAME;

@Repository
@Slf4j
public class UrlCacheRepository {

    @CachePut(cacheNames = URL_CACHE_NAME, key = "#hash")
    public String cacheUrl(String hash, String url) {
        log.info("Caching url: {} -> {}", hash, url);
        return url;
    }
}
