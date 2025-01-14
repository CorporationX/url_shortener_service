package faang.school.urlshortenerservice.repository.url_cash.impl;

import faang.school.urlshortenerservice.repository.url_cash.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UrlCashRepositoryImpl implements UrlCacheRepository {

    private static final String CACHE_KEY_PREFIX = "url_cache:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveUrl(String hash, String longUrl) {
        String cacheKey = CACHE_KEY_PREFIX + hash;

        redisTemplate.opsForValue().set(cacheKey, longUrl);
    }

    @Override
    public Optional<String> getUrl(String hash) {
        String cacheKey = CACHE_KEY_PREFIX + hash;

        String url = redisTemplate.opsForValue().get(cacheKey);
        return Optional.ofNullable(url);
    }

    @Override
    public void deleteUrl(String hash) {
        String cacheKey = CACHE_KEY_PREFIX + hash;

        redisTemplate.delete(cacheKey);
    }
}
