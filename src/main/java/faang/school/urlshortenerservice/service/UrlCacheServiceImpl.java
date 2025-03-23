package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCacheServiceImpl implements UrlCacheService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveUrl(String hash, String url) {
        if (hash == null || url == null) {
            throw new IllegalArgumentException("Hash and URL must not be null");
        }
        log.info("Redis SaveUrl hash {} url {}", hash, url);
        redisTemplate.opsForValue().set(hash, url);
    }

    @Override
    public String getUrl(String hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Hash must not be null");
        }
        return redisTemplate.opsForValue().get(hash);
    }
}
