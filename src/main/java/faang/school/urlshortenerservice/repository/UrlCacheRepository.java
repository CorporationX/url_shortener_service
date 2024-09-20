package faang.school.urlshortenerservice.repository;

import jakarta.annotation.PostConstruct;
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
    private Duration ttlDuration;

    @PostConstruct
    public void init() {
        ttlDuration = Duration.ofDays(redisTtl);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value, ttlDuration);
    }
}