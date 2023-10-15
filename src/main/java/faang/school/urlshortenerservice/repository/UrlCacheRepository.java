package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Slf4j
@Component
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String CACHE_KEY = "url_cache";

    public void save(String hash, String longUrl) {
        redisTemplate.opsForValue().set(CACHE_KEY + ":" + hash, longUrl);
    }

    public String get(String hash) {
        return redisTemplate.opsForValue().get(CACHE_KEY + ":" + hash);
    }
}