package faang.school.urlshortenerservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final Duration ttl;
    private final String prefix;

    public UrlCacheRepositoryImpl(
            RedisTemplate<String, String> redisTemplate,
            @Value("${url.cache.entry-ttl-hours}") Duration ttl,
            @Value("${url.cache.key.prefix}") String prefix
    ) {
        this.redisTemplate = redisTemplate;
        this.ttl = ttl;
        this.prefix = prefix;
    }

    @Override
    public Optional<String> getUrl(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(hash)));
    }

    @Override
    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(key(hash), url, ttl);
    }

    private String key(String hash) {
        return String.format("%s%s", prefix, hash);
    }
}
