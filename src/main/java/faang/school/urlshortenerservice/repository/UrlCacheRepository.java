package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> urlRedisTemplate;

    public void saveUrl(Url url, int ttl) {
        urlRedisTemplate.opsForValue().set(url.getHash(), url, Duration.ofDays(ttl));
    }

    public Url findByHash(String hash) {
        return urlRedisTemplate.opsForValue().get(hash);
    }
}
