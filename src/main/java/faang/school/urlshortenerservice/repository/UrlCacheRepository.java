package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> stringRedisTemplate;

    public Optional<String> get(String hash) {
        String url = stringRedisTemplate.opsForValue().get(hash);
        if (url == null) {
            return Optional.empty();
        } else {
            return Optional.of(url);
        }
    }

    public void set(String hash, String url) {
        stringRedisTemplate.opsForValue().set(hash, url, 60, TimeUnit.MINUTES);
    }
}
