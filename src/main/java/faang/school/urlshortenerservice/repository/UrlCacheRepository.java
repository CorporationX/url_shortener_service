package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${cache.ttl-seconds}")
    private long ttlSeconds;

    private static final String HASH_TO_URL_PREFIX = "hash:";
    private static final String URL_TO_HASH_PREFIX = "url:";

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(HASH_TO_URL_PREFIX + hash, url, Duration.ofSeconds(ttlSeconds));
        redisTemplate.opsForValue().set(URL_TO_HASH_PREFIX + url, hash, Duration.ofSeconds(ttlSeconds));
    }

    public Optional<String> findOriginalUrlByHash(String hash) {
        String url = redisTemplate.opsForValue().get(HASH_TO_URL_PREFIX + hash);
        return Optional.ofNullable(url);
    }

    public Optional<String> findHashByOriginalUrl(String url) {
        String hash = redisTemplate.opsForValue().get(URL_TO_HASH_PREFIX + url);
        return Optional.ofNullable(hash);
    }
}