package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ShortenerProperties shortenerProperties;

    public Url getUrl(String urlAddress) {
        return (Url) redisTemplate.opsForValue().get(urlAddress);
    }

    public void setUrl(String urlAddress, Url url) {
        redisTemplate.opsForValue().set(urlAddress, url, shortenerProperties.url().ttlDays(), TimeUnit.DAYS);
    }
}
