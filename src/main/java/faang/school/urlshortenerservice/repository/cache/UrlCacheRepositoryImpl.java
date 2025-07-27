package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.config.hash.CacheProperties;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public void set(String hash, UrlResponseDto url) {
        redisTemplate.opsForValue().set(hash, url, cacheProperties.ttlDays(), TimeUnit.DAYS);
    }

    @Override
    public UrlResponseDto get(String hash) {
        return (UrlResponseDto) redisTemplate.opsForValue().get(hash);
    }
}
