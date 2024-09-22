package faang.school.urlshortenerservice.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractCacheManager<T> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;
    @Value("${cache.lifetime-minutes}")
    private int minutes;

    protected void addToCache(String key, Map<String, Object> value) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
        log.info("Added to cache by value: {}, TTL: {} minutes", value, minutes);
    }

    protected T getFromCache(String key, String hashKey) {
        var value = redisTemplate.opsForValue().get(hashKey);
        log.info("Getting from cache by value: {}", value);
        return (T) value;
    }

    protected void removeFromCache(String key, List<String> hashKeys) {
        String[] hashes = hashKeys.toArray(new String[0]);
        this.redisTemplate.opsForHash().delete(key, hashes);
        log.info("Removed from cache by key: {}", key);
    }
}
