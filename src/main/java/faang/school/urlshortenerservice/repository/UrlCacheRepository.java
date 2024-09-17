package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        try {
            redisTemplate.opsForValue().set(hash, url);
            log.info("Url was successfully saved in Redis cache: {}", url);
        } catch (Exception e) {
            log.error("Failed to save URL in Redis: {}", e.getMessage());
        }
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
