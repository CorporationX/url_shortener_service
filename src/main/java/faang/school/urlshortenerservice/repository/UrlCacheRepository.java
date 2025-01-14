package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.net.URL;

@RequiredArgsConstructor
@Component
public class UrlCacheRepository {
    private final RedisTemplate<String, URL> redisTemplate;

    public void saveAtRedis(Url url){
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    public URL getFromRedis(String hash){
        return redisTemplate.opsForValue().get(hash);
    }

    public void deleteFormRedis(String hash){
        redisTemplate.delete(hash);
    }
}
