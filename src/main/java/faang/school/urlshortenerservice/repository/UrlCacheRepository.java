package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${url-cache.ttlDays}")
    private final int ttlDays;

    private static final String URL_CACHE_PREFIX = "url_hash:";

    public void put(String hash, String url) {
        redisTemplate.opsForValue().set(URL_CACHE_PREFIX + hash, url, Duration.ofDays(ttlDays));
        log.info("Hash: {}, URL: {} added to Redis cache.", hash, url);
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(URL_CACHE_PREFIX + hash);
    }

    public void evict(String hash) {
        redisTemplate.delete(URL_CACHE_PREFIX + hash);
    }
}
