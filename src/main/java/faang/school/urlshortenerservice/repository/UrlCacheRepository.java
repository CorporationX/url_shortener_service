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

    private final StringRedisTemplate redisTemplate;

    @Value("${url.cache.ttl-seconds}")
    private long ttlInSeconds;

    public Optional<String> findUrlByHash(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash,
                url,
                Duration.ofSeconds(ttlInSeconds)
        );
    }
}
