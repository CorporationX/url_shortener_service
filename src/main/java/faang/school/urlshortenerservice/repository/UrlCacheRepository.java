package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.redis.RedisCacheConfigProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisCacheConfigProperties redisCacheConfigProperties;

    public Optional<String> get(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(url);
    }

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofHours(redisCacheConfigProperties.getTtlTimeHours()));
    }

    public boolean delete(String hash) {
        Boolean deleted = redisTemplate.delete(hash);
        return Boolean.TRUE.equals(deleted);
    }
}