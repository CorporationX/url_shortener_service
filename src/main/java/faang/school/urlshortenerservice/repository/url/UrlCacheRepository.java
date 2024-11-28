package faang.school.urlshortenerservice.repository.url;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String key, String value, long ttl) {
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MINUTES);
    }

    public Optional<String> find(String key) {
        String url = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(url);
    }
}
