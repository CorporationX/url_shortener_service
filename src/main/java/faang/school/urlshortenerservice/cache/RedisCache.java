package faang.school.urlshortenerservice.cache;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class RedisCache {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.redis.ttl}")
    private int ttl;

    public void saveToCache(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, ttl, TimeUnit.HOURS);
    }

    public Optional<String> getFromCache(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }
}
