package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis-url-repo.time-to-leave-hours}")
    private int timeToLive;

    public Optional<String> get(String hash) {
        String value = redisTemplate.opsForValue().get(hash);
        if (value == null) {
            log.info("Hash not found in redis.");
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url, Duration.ofHours(timeToLive));
        } catch (Exception e) {
            log.error("Error saving data in redis.");
            throw new IllegalStateException("Error saving data in redis.");
        }
    }
}
