package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "url:";

    public void save(String hash, String url) {
        String key = PREFIX + hash;
        redisTemplate.opsForValue().set(key, url, 3, TimeUnit.DAYS);
    }

    public Optional<String> find(String hash) {
        String key = PREFIX + hash;
        String url = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(url);
    }
}