package faang.school.urlshortenerservice.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UrlCacheRepository {

    private final StringRedisTemplate redisTemplate;
    private final int cacheTtl;

    public UrlCacheRepository(StringRedisTemplate redisTemplate,
                              @Value("${url.hash.redis.ttl:1}") int cacheTtl) {
        this.redisTemplate = redisTemplate;
        this.cacheTtl = cacheTtl;
    }

    public void saveUrl(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, cacheTtl, TimeUnit.SECONDS);
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
