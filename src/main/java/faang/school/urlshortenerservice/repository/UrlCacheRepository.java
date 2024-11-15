package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.redis.RedisCacheProperties;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisCacheProperties properties;
    private final RedisCacheManager cacheManager;

    public void saveUrl(Url url) {
        Objects.requireNonNull(cacheManager.getCache(properties.getCaches().get("url-cache")))
                .put(url.getHash(), url);
        log.info("url {} saved to cache", url);
    }

    public Url getUrl(String hash) {
        Url url = (Url) Objects.requireNonNull(cacheManager.getCache(properties.getCaches().get("url-cache")))
                .get(hash);
        log.info("get url {} from cache", url);
        return url;
    }
}
