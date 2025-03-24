package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis_url_repo.time_to_leave_hours}")
    private int timeToLive;

    public Optional<String> get(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash))
                .or(() -> {
                    log.info("Hash not found in redis.");
                    return Optional.empty();
                });
    }

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url, Duration.ofHours(timeToLive));
        } catch (Exception e) {
            log.error("Error saving data in redis.");
            throw new IllegalStateException("Error saving data in redis.", e);
        }
    }
}