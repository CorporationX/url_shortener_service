package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class URLCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveUrl(Url url) {

        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    public String getUrl(String hash) {
        return (String) redisTemplate.opsForValue().get(hash);
    }
}
