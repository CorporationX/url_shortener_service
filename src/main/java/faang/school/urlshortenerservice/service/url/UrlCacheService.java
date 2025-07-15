package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlCacheService {

    @Value("${url-cache.ttl}")
    private long ttl;

    private final RedisTemplate<String, Url> redis;

    public void saveUrl(String hash, Url url) {
        redis.opsForValue().set(hash, url, ttl, TimeUnit.DAYS);
    }

    public Url getUrl(String hash) {
        return redis.opsForValue().get(hash);
    }
}
