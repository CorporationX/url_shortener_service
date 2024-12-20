package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.propertis.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlCashRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisProperties redisProperties;

    public void save(String key, String value) {
        long ttl = redisProperties.getUrlTtl();

        if (ttl <= 0) {
            redisTemplate.opsForValue().set(key, value);
        } else {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.HOURS);
        }
    }

    public Optional<String> getValue(String kay) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(kay));
    }
}
