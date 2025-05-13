package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = "urls")
public class UrlCacheRepository {

    @CachePut(key = "#result.id")
    public Url cacheUrl(Url url) {
        return url;
    }
}
