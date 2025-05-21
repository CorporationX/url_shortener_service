package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.app.HashCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final HashCacheProperties hashCacheProperties;

    public void save(String hash, String originalUrl) {
        redisTemplate.opsForValue().set("hash:" + hash, originalUrl,
                hashCacheProperties.getCacheTtl(), TimeUnit.DAYS);
        redisTemplate.opsForValue().set("url:" + originalUrl, hash,
                hashCacheProperties.getCacheTtl(), TimeUnit.DAYS);
    }

    public String findByHash(String hash) {
        return redisTemplate.opsForValue().get("hash:" + hash);
    }

    public String findHashByUrl(String originalUrl) {
        return redisTemplate.opsForValue().get("url:" + originalUrl);
    }
}
