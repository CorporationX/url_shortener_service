package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String URL_KEY_PREFIX = "url:";
    private static final Duration CACHE_TTL = Duration.ofDays(7);

    public void save(String hash, String originalUrl) {
        try {
            String key = URL_KEY_PREFIX + hash;
            redisTemplate.opsForValue().set(key, originalUrl, CACHE_TTL);
            log.debug("Saved URL mapping to Redis: {} -> {}", hash, originalUrl);
        } catch (Exception e) {
            log.error("Failed to save URL mapping to Redis: {} -> {}", hash, originalUrl, e);
        }
    }

    public Optional<String> findByHash(String hash) {
        try {
            String key = URL_KEY_PREFIX + hash;
            String url = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved URL from Redis for hash {}: {}", hash, url);
            return Optional.ofNullable(url);
        } catch (Exception e) {
            log.error("Failed to retrieve URL from Redis for hash: {}", hash, e);
            return Optional.empty();
        }
    }
}
