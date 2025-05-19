package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private static final String PREFIX = "url:";

    private final StringRedisTemplate redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(PREFIX + hash, url, 1, TimeUnit.DAYS);
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(PREFIX + hash);
    }

    public void delete(String hash) {
        redisTemplate.delete(PREFIX + hash);
    }
}
