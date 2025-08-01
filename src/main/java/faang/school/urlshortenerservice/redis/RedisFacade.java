package faang.school.urlshortenerservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisFacade {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.cashingDuration}")
    private int cachingDuration;

    public void saveToRedisCache (String key, Object value) {
        redisTemplate.opsForValue()
                .set(key, value, Duration.ofHours(this.cachingDuration));
    }

    public void increasePopularity (String hash) {
        redisTemplate.opsForZSet()
                .incrementScore("popular:urls", hash , 1);
    }

    public Object checkCache (String key) {
       return redisTemplate.opsForValue().get(key);
    }
}
