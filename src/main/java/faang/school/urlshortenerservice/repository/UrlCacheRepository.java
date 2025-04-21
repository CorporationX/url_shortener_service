package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    @Value("${spring.cache.timeout}")
    private int timeout;

    private final StringRedisTemplate redisTemplate;

    public void save(String url, String hash) {
        redisTemplate.opsForValue().set(hash, url, timeout, TimeUnit.DAYS);
    }

    public Optional<String> findUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}
