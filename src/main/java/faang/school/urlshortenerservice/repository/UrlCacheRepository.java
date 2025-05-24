package faang.school.urlshortenerservice.repository;

import io.micrometer.core.annotation.Timed;
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

    @Timed(value = "find_url_by_hash_timer", description = "Time taken to find URL by hash in Redis",
            histogram = true, percentiles = {0.5, 0.95})
    public Optional<String> findUrlByHash(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }

    @Timed(value = "save_to_url_cache_repository_timer", description = "Time taken to save to URL cache repository",
            histogram = true, percentiles = {0.5, 0.95})
    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash,
                url,
                Duration.ofSeconds(ttlInSeconds)
        );
    }
}
