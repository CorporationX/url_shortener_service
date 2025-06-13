package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.UrlProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private static final String URL_PREFIX = "short:url:";
    private final StringRedisTemplate redisTemplate;
    private final UrlProperties urlProperties;

    public Optional<String> findByHash(String hash) {
        String url = redisTemplate.opsForValue().get(URL_PREFIX + hash);
        return Optional.ofNullable(url);
    }

    public void save(String hash, String originalUrl) {
        Duration ttl = urlProperties.getTtl();
        redisTemplate.opsForValue().set(URL_PREFIX + hash, originalUrl, ttl);
    }

    public void delete(String hash) {
        redisTemplate.delete(URL_PREFIX + hash);
    }
}