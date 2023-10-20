package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Cacheable(value = "hashCash", key = "#hash")
    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
        log.info("Url was successfully saved {}", url);
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
