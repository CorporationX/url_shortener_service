package faang.school.urlshortenerservice.repository.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.redis-cache.hours-to-expire:24}")
    private int hoursToExpire;

    public void save(String hash, String origUrl) {
        try {
            redisTemplate.opsForValue().set(hash, origUrl, hoursToExpire, TimeUnit.HOURS);
            log.info("{}:{} saved to Redis", hash, origUrl);
        } catch (RuntimeException e) {
            log.error("Occurred exception during save to Redis", e);
        }
    }

    public Optional<String> getUrl(String hash) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
        } catch (RuntimeException e) {
            log.error("Occurred exception during get value from Redis", e);
        }
        return Optional.empty();
    }
}
