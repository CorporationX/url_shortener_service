package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private static final long TTL_HOURS = 24;

    public void save(String hash, String url) {
        redisTemplate.opsForValue()
                .set(hash, url, TTL_HOURS, TimeUnit.HOURS);
    }

    public Optional<String> findByHash(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }
}
