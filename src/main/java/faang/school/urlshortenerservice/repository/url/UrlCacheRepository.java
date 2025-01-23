package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis-url-cache.time-to-leave-hours}")
    private int timeToLeaveInHours;

    public Optional<String> get(String hash) {
        String value = redisTemplate.opsForValue().get(hash);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url, Duration.ofHours(timeToLeaveInHours));
        } catch (Exception ex) {
            log.error("Error saving url to redis", ex);
            throw new IllegalStateException("Error getting saving url to redis");
        }
    }
}
