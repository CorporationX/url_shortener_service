package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public String findUrlFromCache(String hash) {
        log.debug("Searching for URL by hash: {}", hash);
        return redisTemplate.opsForValue().get(hash);
    }

    public void saveToCache(String hash, String url) {
        log.debug("Saving URL to cache for hash: {}", hash);
        redisTemplate.opsForValue().set(hash, url);
    }
}
