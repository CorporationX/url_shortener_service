package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepository {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void saveToCache(Url url) {
        log.info("Saving url to cache: {}", url);
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
    }

    public String getUrlByHash(String hash) {
        log.info("Getting url by hash: {}", hash);
        String url = redisTemplate.opsForValue().get(hash);
        if (url == null) {
            log.info("Url not found in cache, getting from BD: {}", hash);
            String existingUrl = urlRepository
                    .findByHash(hash)
                    .orElseThrow(() -> {
                        log.info("Url not found in BD by hash: {}", hash);
                        return new UrlNotFoundException("Url not found by hash: " + hash);
                    })
                    .getUrl();
            log.info("Got url from BD and saved to cache: {}", existingUrl);
            redisTemplate.opsForValue().set(hash, existingUrl);
            return existingUrl;
        }
        log.info("Got url from cache: {}", url);
        return url;
    }
}
