package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlShortenerRedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final String ORIGINAL_URL_KEY = "originalUrl:";
    private final String HASH_URL_KEY = "urlHash:";

    @Value("${app.url-cache-ttl-days}")
    private int urlCacheTtlDays;

    public void addUrlHash(String originalUrl, String hash) {
        add(HASH_URL_KEY, originalUrl, hash);
    }

    public void addOriginalUrl(String originalUrl, String shortUrl) {
        add(ORIGINAL_URL_KEY, getHashFromUrl(shortUrl), originalUrl);
    }

    public String getUrlHash(String originalUrl) {
        return hashOperations.get(HASH_URL_KEY, originalUrl);
    }

    public String getOriginalUrl(String shortUrl) {
        return hashOperations.get(ORIGINAL_URL_KEY, getHashFromUrl(shortUrl));
    }

    private void add(String key, String hashKey, String value) {
        hashOperations.put(key, hashKey, value);
        redisTemplate.expire(key, Duration.ofDays(urlCacheTtlDays));
    }

    private String getHashFromUrl(String shortUrl) {
        return shortUrl.substring(shortUrl.lastIndexOf("/") + 1);
    }
}
