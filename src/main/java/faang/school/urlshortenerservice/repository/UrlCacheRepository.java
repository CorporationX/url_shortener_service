package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${url.cache.ttl:86400}") // 24 hours by default
    private long cacheTtlSeconds;

    /**
     * Сохраняет ассоциацию хэша и URL в Redis
     *
     * @param hash хэш короткой ссылки
     * @param url  оригинальный URL
     */
    public void save(String hash, String url) {
        log.debug("Saving to Redis cache: hash={}, url={}", hash, url);

        try {
            redisTemplate.opsForValue().set(
                    hash,
                    url,
                    Duration.ofSeconds(cacheTtlSeconds)
            );
            log.debug("Successfully saved to Redis cache: hash={}", hash);
        } catch (Exception e) {
            log.error("Failed to save to Redis cache: hash={}", hash, e);
            throw new RuntimeException("Failed to save URL to cache", e);
        }
    }

    /**
     * Получает URL по хэшу из Redis
     *
     * @param hash хэш короткой ссылки
     * @return оригинальный URL или null, если не найден
     */
    public String get(String hash) {
        log.debug("Getting from Redis cache: hash={}", hash);

        try {
            String url = redisTemplate.opsForValue().get(hash);
            log.debug("Retrieved from Redis cache: hash={}, found={}", hash, url != null);
            return url;
        } catch (Exception e) {
            log.error("Failed to get from Redis cache: hash={}", hash, e);
            return null;
        }
    }

    /**
     * Удаляет ассоциацию из Redis
     *
     * @param hash хэш короткой ссылки
     */
    public void delete(String hash) {
        log.debug("Deleting from Redis cache: hash={}", hash);

        try {
            redisTemplate.delete(hash);
            log.debug("Successfully deleted from Redis cache: hash={}", hash);
        } catch (Exception e) {
            log.error("Failed to delete from Redis cache: hash={}", hash, e);
        }
    }
}