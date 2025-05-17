package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.exceptions.CacheOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${urlShortener.cache.ttl-days}")
    private int ttlDays;

    public void save(String hash, String originalUrl) {
        try {
            redisTemplate.opsForValue().set(
                    hash,
                    originalUrl,
                    ttlDays,
                    TimeUnit.DAYS
            );
            log.debug("Saved URL in cache: hash={}, originalUrl={}", hash, originalUrl);
        } catch (Exception e) {
            log.error("Failed to save URL in cache: hash={}", hash, e);
            throw new CacheOperationException("Failed to save URL in cache", e);
        }
    }

    public Optional<String> findByHash(String hash) {
        try {
            String originalUrl = redisTemplate.opsForValue().get(hash);
            if (originalUrl != null) {
                log.debug("Cache hit for hash: {}", hash);
            } else {
                log.debug("Cache miss for hash: {}", hash);
            }
            return Optional.ofNullable(originalUrl);
        } catch (Exception e) {
            log.error("Failed to retrieve URL from cache: hash={}", hash, e);
            throw new CacheOperationException("Failed to retrieve URL from cache", e);
        }
    }

    public boolean delete(String hash) {
        try {
            Boolean deleted = redisTemplate.delete(hash);
            if (Boolean.TRUE.equals(deleted)) {
                log.debug("Deleted cache entry for hash: {}", hash);
                return true;
            }
            log.debug("No cache entry found to delete for hash: {}", hash);
            return false;
        } catch (Exception e) {
            log.error("Failed to delete URL from cache: hash={}", hash, e);
            throw new CacheOperationException("Failed to delete URL from cache", e);
        }
    }
}

