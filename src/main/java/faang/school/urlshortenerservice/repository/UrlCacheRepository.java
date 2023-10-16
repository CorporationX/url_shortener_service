package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${redis_cache.ttl}")
    private int ttl;

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl(), Duration.ofMinutes(ttl));

        log.info("Url {} with {} saved to cache", url.getHash(), url.getUrl());
    }

    public String getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
