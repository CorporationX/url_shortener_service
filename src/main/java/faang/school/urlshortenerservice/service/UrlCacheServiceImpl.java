package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class UrlCacheServiceImpl implements UrlCacheService {
    @Value("${redis.ttl.seconds:3600}")
    private final long redisTtlSeconds;

    private final RedisTemplate<String, String> redisTemplate;


    @Override
    public void saveNewPair(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl, redisTtlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    @Override
    public void deletePairByHash(String hash) {
        redisTemplate.delete(hash);
    }
}
