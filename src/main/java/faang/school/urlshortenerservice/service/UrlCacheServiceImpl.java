package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlCacheServiceImpl implements UrlCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisProperties properties;

    @Override
    public void saveUrl(String hash, String url) {
        if (hash == null || url == null) {
            throw new IllegalArgumentException("Hash and URL must not be null");
        }
        redisTemplate.opsForValue().set(hash, url, properties.getTtl(), TimeUnit.HOURS);
    }

    @Override
    public String getUrl(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Hash must not be null");
        }
        return redisTemplate.opsForValue().get(hash);
    }
}
