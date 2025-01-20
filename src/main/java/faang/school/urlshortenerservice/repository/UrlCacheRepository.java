package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    public final static String WARNING_MESSAGE = "Redis isn't available, hashing not available: {}";
    public final static String WARNING_MESSAGE_BY_HASH = "Redis isn't available, the original URL was not retrieved from the cache by hash {}";

    @Value("${redis.cache-ttl}")
    private long cacheTtl;

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url, cacheTtl, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn(WARNING_MESSAGE, hash, e);
        }
    }

    public String getCacheValueByHash(String hash) {
        try {
            return redisTemplate.opsForValue().get(hash);
        } catch (Exception e) {
            log.warn(WARNING_MESSAGE_BY_HASH, hash, e);
            return null;
        }
    }
}
