package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.hash.UrlShortenerConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UrlCacheRepository {
    private final String urlCache;
    private final StringRedisTemplate stringRedisTemplate;
    private final long cacheTtl;

    public UrlCacheRepository(UrlShortenerConfig urlShortenerConfig,
                              StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.urlCache = urlShortenerConfig.getUrlCacheKey();
        this.cacheTtl = urlShortenerConfig.getCacheTtl();
    }

    public void save(String hash, String url) {
        stringRedisTemplate.opsForValue().set(
                urlCache + ":" + hash,
                url,
                cacheTtl,
                TimeUnit.DAYS);
    }

    public String get(String hash) {
        return stringRedisTemplate.opsForValue().get(urlCache + ":" + hash);
    }
}
