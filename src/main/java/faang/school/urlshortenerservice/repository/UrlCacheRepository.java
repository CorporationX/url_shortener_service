package faang.school.urlshortenerservice.repository;
import io.lettuce.core.RedisConnectionException;
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

    @Value("${spring.data.cache.redis.time-to-live}")
    private long timeToLive;

    public void save(String url, String hash) {
        try {
            redisTemplate.opsForValue()
                    .set(url, hash, timeToLive, TimeUnit.SECONDS);
            redisTemplate.opsForValue()
                    .set(hash, url, timeToLive, TimeUnit.SECONDS);
        } catch (RedisConnectionException e) {
            log.warn("Failed to connect Redis. Cache will be skipped for URL: {}", url, e);
        }
    }

    public Optional<String>  findUrlByHash(String hash) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
        } catch (RedisConnectionException e) {
            log.warn("Redis is unavailable. Return empty result for Hash: {}", hash, e);
        }
        return Optional.empty();
    }

    public Optional<String> findHashByUrl(String url) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(url));
        } catch (RedisConnectionException e) {
            log.warn("Redis is unavailable. Return empty result for Hash: {}", url, e);
        }
        return Optional.empty();
    }
}