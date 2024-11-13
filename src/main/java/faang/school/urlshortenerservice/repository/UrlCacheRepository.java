package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {

    private final RedisTemplate<String, Url> redisTemplate;

    public void save(String hash, Url url) {
        redisTemplate.opsForValue().set(hash, url);
    }

    public Url getUrl(String hash) {
        return redisTemplate.opsForValue().get(hash);
    }

}
