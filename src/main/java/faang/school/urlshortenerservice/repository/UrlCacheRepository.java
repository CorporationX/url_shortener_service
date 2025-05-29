package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    @Value("${spring.data.redis.key-prefix.url}")
    private String keyPrefix;

    private final StringRedisTemplate redisTemplate;

    public void saveUrl(String hash, String url, Duration ttl) {
        redisTemplate.opsForValue().set(createUrlKey(hash), url, ttl);
    }

    public Optional<String> findUrlAndExpire(String hash, Duration ttl) {
        return Optional.ofNullable(redisTemplate.opsForValue().getAndExpire(createUrlKey(hash), ttl));
    }

    private String createUrlKey(String hash) {
        return keyPrefix + hash;
    }
}
