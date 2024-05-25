package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    @Cacheable(value = "hashCash", key = "#hash")
    public Optional<String> getUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }

    /**
     * Checks if the given URL exists in the Redis cache.
     * @param url The URL to check in the cache.
     * @return An Optional containing the cached URL if it exists, otherwise an empty Optional.
     */
    public Optional<String> existsUrl(String url) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(url));
    }

}