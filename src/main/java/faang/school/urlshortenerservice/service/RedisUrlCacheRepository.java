package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUrlCacheRepository implements UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(hash, longUrl);
    }

    @Override
    public String find(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
