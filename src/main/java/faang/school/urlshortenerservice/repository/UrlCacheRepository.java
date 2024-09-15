package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    @Value("${spring.data.redis.ttl}")
    private int redisTtl;
    private final RedisTemplate<String, String> redisTemplate;

    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofDays(redisTtl));
    }
}