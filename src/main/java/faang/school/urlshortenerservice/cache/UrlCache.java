package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCache {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${url.cache.expiration-time}")
    private long expirationTime;

    public Optional<String> getUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }

    public void saveUrlByHash(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, expirationTime, TimeUnit.SECONDS);
    }
}
