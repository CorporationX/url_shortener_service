package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String hash, String url, Duration timeout) {
        redisTemplate.opsForValue().set(hash, url, timeout);
    }

    public Optional<String> get(String hash) {
        return Optional.ofNullable((String) redisTemplate.opsForValue().get(hash));
    }
}
