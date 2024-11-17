package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {

    @Value("${spring.data.redis.time-to-live}")
    long timeToLive;

    private final RedisTemplate<String, String> redisTemplate;

    public String getUrlByHash(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

    public String getHashByUrl(String url) {
        return redisTemplate.opsForValue().get(url);
    }

    public void save(Url url) {
        redisTemplate.opsForValue()
                .set(url.getHash(), url.getUrl(), timeToLive, TimeUnit.SECONDS);
        redisTemplate.opsForValue()
                .set(url.getUrl(), url.getHash(), timeToLive, TimeUnit.SECONDS);
    }
}
