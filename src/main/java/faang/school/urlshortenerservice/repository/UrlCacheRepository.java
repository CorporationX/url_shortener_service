package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String hash, String url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    @Cacheable(value = "hashCache", key = "#hash")
    public Optional<String> getUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}