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

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
        log.info("URL saved successfully {}", url);
    }

    @Cacheable(value = "hashCash", key = "#hash")
    public String getUrl(String hash) {
        String hashCache = redisTemplate.opsForValue().get(hash);
        if (hashCache != null) {
            log.info("Hash {} found in cache", hash);
        }
        else {
            log.warn("Hash {} not found in cache", hash);
        }
        return hashCache;
    }
}