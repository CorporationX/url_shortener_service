package faang.school.urlshortenerservice.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.data.ttl.hour}")
    private Long ttl;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofHours(ttl));
    }

    public String find(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public void delete(String hash) {
        redisTemplate.delete(hash);
    }
}
