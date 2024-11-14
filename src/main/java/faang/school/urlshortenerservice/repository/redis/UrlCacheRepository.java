package faang.school.urlshortenerservice.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final StringRedisTemplate stringRedisTemplate;

    public void save(String hash, String url) {
        stringRedisTemplate.opsForValue().set(hash, url, 7, TimeUnit.DAYS);
    }

    public String get(String hash) {
        return stringRedisTemplate.opsForValue().get(hash);
    }
}
