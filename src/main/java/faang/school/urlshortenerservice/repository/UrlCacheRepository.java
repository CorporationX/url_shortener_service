package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private static final long URL_EXPIRATION_TIME = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, URL_EXPIRATION_TIME, TIME_UNIT);
    }

    public Optional<String> findByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}

