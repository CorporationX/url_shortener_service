package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveToCache(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
        redisTemplate.expire(hash, Duration.ofSeconds(redisProperties.ttlSeconds()));
        log.info("Short url pair {} : {} saved to Redis cache", hash, url);
    }

    public Optional<String> getFromCache(String hash) {
        String url = redisTemplate.opsForValue().get(hash);

        return Optional.ofNullable(url);
    }
}
