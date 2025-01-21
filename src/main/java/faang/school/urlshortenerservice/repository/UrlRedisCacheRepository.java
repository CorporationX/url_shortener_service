package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlRedisCacheRepository {
    private final RedisProperties redisProperties;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveUrl(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, redisProperties.ttl(), TimeUnit.SECONDS);
    }

    public Optional<String> findByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}
