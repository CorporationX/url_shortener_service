package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Optional<String> findUrlByHash(String hash) {
        try {
            String url = redisTemplate.opsForValue().get(hash);
            return Optional.ofNullable(url);
        } catch (Exception e) {
            log.error("Error retrieving URL from Redis for hash: {}", hash, e);
            return Optional.empty();
        }
    }

    public void saveUrl(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url);
            log.debug("Saved URL to Redis cache for hash: {}", hash);
        } catch (Exception e) {
            log.error("Error saving URL to Redis for hash: {}", hash, e);
        }
    }
}
