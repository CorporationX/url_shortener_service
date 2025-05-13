package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public Optional<String> findByHash(String hash) {
        try {
            String url = redisTemplate.opsForValue().get(hash);
            return Optional.ofNullable(url);
        } catch (Exception e) {
            log.error("Redis lookup error for hash: {}", hash, e);
            return Optional.empty();
        }
    }

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofDays(30));
    }
}
