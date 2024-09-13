package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class URLCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.data.cache.redis.time-to-live}")
    private long timeToLive;

    public void save(String url, String hash) {
        redisTemplate.opsForValue()
                .set(url, hash, timeToLive, TimeUnit.SECONDS);
    }

    public Optional<String>  findUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }

    public Optional<String> findHashByUrl(String url) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(url));
    }
}