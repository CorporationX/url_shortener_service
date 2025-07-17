package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String hash, UrlDto url, int ttl) {
        redisTemplate.opsForValue().set(hash, url, ttl, TimeUnit.DAYS);
    }
}
