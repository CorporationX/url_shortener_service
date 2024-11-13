package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${url.cache.ttl.timeout}")
    private long timeout;

    @Value("${url.cache.ttl.time-unit}")
    private String timeUnit;

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl(), timeout, TimeUnit.valueOf(timeUnit));
        log.info("saved url {} with hash {} to cache", url.getUrl(), url.getHash());
    }

    public Optional<String> findUrlByHash(String hash) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(hash));
    }
}
