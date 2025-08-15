package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ShortUrlCacheServiceImpl implements ShortUrlCacheService {
    @Value("${spring.shortener.ttl}")
    private int ttl;
    @Value("${spring.shortener.prefix}")
    private String cachePrefix;
    private final StringRedisTemplate cache;

    @Override
    public String get(String code) {
        return cache.opsForValue().get(code);
    }

    @Override
    public void set(String code, String url) {
        cache.opsForValue().set(cachePrefix + code, url, ttl, TimeUnit.SECONDS);
    }
}
