package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.properties.short_url.ShortUrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ShortUrlProperties shortUrlProperties;

    public void save(String hash, String originalUrl, long ttl, TimeUnit timeUnit) {
        String shortUrlKey = "%s::%s".formatted(shortUrlProperties.getCacheSettings().getDefaultCacheName(), hash);
        redisTemplate.opsForValue().set(shortUrlKey, originalUrl, ttl, timeUnit);
    }

    public void save(String hash, String originalUrl) {
        int urlCacheTtl = shortUrlProperties.getCacheSettings().getDefaultTtlMinutes();
        String shortUrlKey = "%s::%s".formatted(shortUrlProperties.getCacheSettings().getDefaultCacheName(), hash);
        redisTemplate.opsForValue().set(shortUrlKey, originalUrl, urlCacheTtl, TimeUnit.MINUTES);
    }

    public void updateShortUrlRequestStats(String hash) {
        String shortUrlStatsZSetName = shortUrlProperties.getCacheSettings().getShortUrlRequestStatsCacheName();
        redisTemplate.opsForZSet()
                .incrementScore(shortUrlStatsZSetName, hash,1);
    }

    public Set<String> getPopularUrlHashes() {
        String shortUrlStatsZSetName = shortUrlProperties.getCacheSettings().getShortUrlRequestStatsCacheName();
        int topHashCount = shortUrlProperties.getCacheSettings().getPopularHashMaxCount();
        return redisTemplate.opsForZSet().reverseRange(shortUrlStatsZSetName, 0, topHashCount - 1);
    }

    public void resetShortUrlRequestStats() {
        String shortUrlStatsZSetName = shortUrlProperties.getCacheSettings().getShortUrlRequestStatsCacheName();
        redisTemplate.delete(shortUrlStatsZSetName);
    }
}
