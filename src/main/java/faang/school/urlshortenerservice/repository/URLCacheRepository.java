package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class URLCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.cache.redis.time-to-live}")
    private long timeToLive;

    public void save(URL url) {
        redisTemplate.opsForValue()
                .set(url.getHash(), url, timeToLive, TimeUnit.SECONDS);
    }

    public URL find(String hash) {
        Object url = redisTemplate.opsForValue().get(hash);
        return url != null ? (URL) url : null;
    }
}