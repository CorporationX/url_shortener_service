package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, URL> redisTemplate;

    public void saveAtRedis(Url url) {
        log.info("add new key-value in redis with 1 day time to live");
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl(), 1, TimeUnit.DAYS);
    }

    public URL getFromRedis(String hash) {
        log.info("get value by key from redis");
        return redisTemplate.opsForValue().get(hash);
    }

    public void deleteFormRedis(String hash) {
        log.info("remove value from redis by key");
        redisTemplate.delete(hash);
    }
}
