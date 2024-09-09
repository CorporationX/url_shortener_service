package faang.school.urlshortenerservice.cache.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public abstract class AbstractCacheManager<T> {

    protected final ObjectMapper mapper;
    protected final RedisTemplate<String, Object> redisTemplate;

    protected void put(String key, Map<String, T> values) {
        this.redisTemplate.opsForHash().putAll(key, values);
    }

    protected T get(String key, String hashKey) {
        return (T) this.redisTemplate.opsForHash().get(key, hashKey);
    }

    protected void setExpire(String key, long ttlMinutes) {
        this.redisTemplate.expire(key, ttlMinutes, TimeUnit.MINUTES);
    }

    protected void delete(String key, List<String> hashKeys) {
        String[] hashes = hashKeys.toArray(new String[0]);
        this.redisTemplate.opsForHash().delete(key, hashes);
    }
}
