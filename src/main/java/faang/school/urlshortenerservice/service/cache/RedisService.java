package faang.school.urlshortenerservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService<T> implements CacheService<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void put(String key, T value, Duration time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    @Override
    public Optional<T> getValue(String key, Class<T> clazz) {
        var value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value)
                .map(t -> objectMapper.convertValue(t, clazz));
    }

    @Override
    public long getExpire(String key, TimeUnit timeUnit) {
        Long expire = redisTemplate.getExpire(key, timeUnit);
        return Objects.requireNonNullElse(expire, 0L);
    }

    @Override
    public boolean delete(String key) {
        Boolean deleted = redisTemplate.delete(key);
        return Objects.requireNonNullElse(deleted, false);
    }

    @Override
    public long incrementAndGet(String key) {
        Long counter = redisTemplate.opsForValue().increment(key);
        return Objects.requireNonNullElse(counter, 0L);
    }

    @Override
    public long addExpire(String key, Duration duration) {
        long currentTtl = getExpire(key, TimeUnit.MILLISECONDS);
        currentTtl += duration.toMillis();
        redisTemplate.expire(key, currentTtl, TimeUnit.MILLISECONDS);
        return currentTtl;
    }
}
