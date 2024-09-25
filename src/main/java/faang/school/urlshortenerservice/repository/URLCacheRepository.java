package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.RedisCacheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class URLCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveUrl(Url url) {
        redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
        log.info("Saved url {} with hash {} in redis", url.getUrl(), url.getHash());
    }

    public String getUrl(String hash) {
        log.info("Getting url for hash {} from redis", hash);
        try {
            return redisTemplate.opsForValue().get(hash);
        }
        catch (Exception e) {
            log.info("Exception while getting url for hash {}", hash);
            throw new RedisCacheException("Exception while getting url for hash " + hash, e);
        }
    }
}
