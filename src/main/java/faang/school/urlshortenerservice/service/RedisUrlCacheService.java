package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.ShortUrl;
import faang.school.urlshortenerservice.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisUrlCacheService {

    private final static String SHORT_URL_PREFIX = "short_url";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapperUtil objectMapper;

    @Value(value = "${data.redis.default-ttl-seconds:3600}")
    private long defaultTtlSeconds;

    public void cacheUrl(ShortUrl shortUrl) {
        cacheUrl(shortUrl, defaultTtlSeconds);
    }

    public void cacheUrl(ShortUrl shortUrl, long ttlSeconds) {
        String key = getKey(shortUrl.getHash());
        String json = objectMapper.writeAsString(shortUrl);
        redisTemplate.opsForValue()
                .set(key, json, Duration.ofSeconds(ttlSeconds));
    }

    public Optional<ShortUrl> getUrl(String hash) {
        String key = getKey(hash);
        String json = redisTemplate.opsForValue()
                .getAndExpire(key, Duration.ofSeconds(defaultTtlSeconds));
        return json == null ? Optional.empty() : Optional.of(objectMapper.readValueAs(json, ShortUrl.class));
    }

    public void deleteUrlFromCacheAllIn(List<String> hash) {
        List<String> keys = hash.stream().map(this::getKey).toList();
        redisTemplate.delete(keys);
    }

    private String getKey(String hash) {
        return SHORT_URL_PREFIX + ":" + hash;
    }
}