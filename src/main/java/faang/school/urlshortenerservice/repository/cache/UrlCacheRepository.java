package faang.school.urlshortenerservice.repository.cache;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {
    private final RedisTemplate<String, Url> urlRedisTemplate;

    public void save(Url url) {
        urlRedisTemplate.opsForValue().set(url.getHash(), url);
    }
}
