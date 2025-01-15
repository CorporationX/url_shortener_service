package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveToCache(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    public String getUrlByHash(String hash) {
        String url = redisTemplate.opsForValue().get(hash);
        if (url == null) {
            url = urlRepository
                    .findByHash(hash)
                    .orElseThrow(() -> new IllegalArgumentException("Url not found by hash: " + hash))
                    .getUrl();
            redisTemplate.opsForValue().set(hash, url);
        }
        return url;
    }
}
