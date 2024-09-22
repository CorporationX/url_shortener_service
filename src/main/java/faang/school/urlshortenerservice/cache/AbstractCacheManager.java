package faang.school.urlshortenerservice.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
        log.info("Added to cache by key: {}, value: {}, TTL: {} minutes", key, value, minutes);
    }

    public Object getFromCache(String key, String hashKey) {
        Object value = redisTemplate.opsForValue().get(key);
        log.info("Getting from cache by key: {}, value {}", key, value);
        return value;
    }

    public void removeFromCache(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
        log.info("Removed from cache by key: {}", key);
    }
}
