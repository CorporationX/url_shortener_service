package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CacheRepositoryRedisImpl implements CacheRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final Duration cacheTtl;

    @Override
    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key, value, cacheTtl);
    }

    @Override
    public Optional<String> get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(value);
    }
}
