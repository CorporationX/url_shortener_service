package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.config.properties.url.UrlCacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepositoryImpl implements UrlCacheRepository {

    private final UrlCacheProperties urlCacheProperties;

    private final StringRedisTemplate redisTemplate;

    @Override
    public String get(String hash) {
        if (hash == null || hash.isBlank()) {
            return null;
        }
        return redisTemplate.opsForValue().get(buildKey(hash));
    }

    @Override
    public void put(String hash, String url) {
        if (hash == null || hash.isBlank() || url == null || url.isBlank()) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(buildKey(hash), url, urlCacheProperties.ttl());
        } catch (RuntimeException e) {
            log.warn("Redis put failed for hash={}", hash, e);
        }
    }

    @Override
    public void delete(List<String> hashes) {
        if (hashes == null || hashes.isEmpty()) {
            return;
        }
        try {
            List<String> keys = hashes.stream()
                    .filter(hash -> hash != null && !hash.isBlank())
                    .map(this::buildKey)
                    .toList();
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (RuntimeException e) {
            log.warn("Redis delete failed", e);
        }
    }

    private String buildKey(String hash) {
        return urlCacheProperties.prefix() + hash;
    }
}
