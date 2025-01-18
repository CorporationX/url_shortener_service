package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> redisTemplate;

    @Value("${spring.data.redis.url-ttl}")
    private int ttl;

    public void save(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url, Duration.ofDays(ttl));
    }

    public Url findByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }
}
