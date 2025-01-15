package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.properties.short_url.UrlCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final UrlCacheProperties urlCacheProperties;

    public void save(String hash, String originalUrl, int ttl, TimeUnit timeUnit) {
        String shortUrlKey = "%s::%s".formatted(urlCacheProperties.getDefaultCacheName(), hash);
        redisTemplate.opsForValue().set(shortUrlKey, originalUrl, ttl, timeUnit);
    }

    public Optional<String> getOriginalUrl(String hash) {
        String shortUrlKey = "%s::%s".formatted(urlCacheProperties.getDefaultCacheName(), hash);
        return Optional.ofNullable(redisTemplate.opsForValue().get(shortUrlKey));
    }

    public void updateShortUrlRequestStats(String hash) {
        String shortUrlStatsZSetName = urlCacheProperties.getPopularCacheName();
        redisTemplate.opsForZSet().incrementScore(shortUrlStatsZSetName, hash,1);
    }

    public Set<String> getPopularUrlHashes() {
        String shortUrlStatsZSetName = urlCacheProperties.getPopularCacheName();
        int topHashCount = urlCacheProperties.getPopularHashMaxCount();
        return redisTemplate.opsForZSet().reverseRange(shortUrlStatsZSetName, 0, topHashCount - 1);
    }

    public void resetShortUrlRequestStats() {
        String shortUrlStatsZSetName = urlCacheProperties.getPopularCacheName();
        redisTemplate.delete(shortUrlStatsZSetName);
    }
}
