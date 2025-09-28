package faang.school.urlshortenerservice.utilities;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UrlRedisCache {
    private final StringRedisTemplate redisTemplate;

    @Value("${cache.expiration-hours}")
    private int amountOfTime;


    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofHours(amountOfTime));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Long deleteAll(List<String> keys) {
        return redisTemplate.delete(keys);
    }
}
