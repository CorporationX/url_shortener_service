package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static java.util.concurrent.TimeUnit.HOURS;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    @Value("app.hash.expiration")
    private long expirationTime;

    public void saveUrl(Url url){
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl(), expirationTime, HOURS);
    }
    public String getUrl(String key){
        return redisTemplate.opsForValue().get(key);
    }
}