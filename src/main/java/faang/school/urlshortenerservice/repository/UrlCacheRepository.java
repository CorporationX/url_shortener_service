package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    @Value("${ttl.hour.url}")
    private int ttlHours;

    private static final String PREFIX = "url:";

    public void save(String hash, String url) {
        try {
            Duration ttl = ttlHours > 0 ? Duration.ofHours(ttlHours) : Duration.ZERO;
            log.info("Saving URL in cache with TTL: {}", ttl);

            redisTemplate.opsForValue().set(PREFIX + hash, url, ttl);
        } catch (Exception e) {
            log.error("URL caching error: {}", e.getMessage());
        }
    }

    public Optional<String> findByHash(String hash) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(PREFIX + hash));
        } catch (Exception e) {
            log.error("Error loading URL from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }
}