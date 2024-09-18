package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${executor.hash-cache.prefix}")
    private String cacheKey;

    public void save(String hash, String url) {
        redisTemplate.opsForHash().put(cacheKey, hash, url);
    }

    public Optional<String> findByHash(String hash) {
        return Optional.ofNullable((String) redisTemplate.opsForHash().get(cacheKey, hash));
    }
}