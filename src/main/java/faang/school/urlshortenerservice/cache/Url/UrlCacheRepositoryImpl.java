package faang.school.urlshortenerservice.cache.Url;

import faang.school.urlshortenerservice.cache.UrlCacheRepository;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
@AllArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${hash.cache.ttl_days}")
    private final int ttl;

    @Override
    public void save(String hash, Url url) {
        redisTemplate.opsForValue().set(hash, url, ttl, TimeUnit.DAYS);
    }

    @Override
    public Url get(String hash) {
        try {
            Url url = (Url) redisTemplate.opsForValue().get(hash);
            if (url == null) {
                log.debug("Url with hash: {} was not found in cache", hash);
            } else {
                log.debug("Url with hash: {} was found in cache", hash);
            }

            return url;
        } catch (Exception e) {
            log.error("While getting url, something gone wrong for hash {}", hash, e);
            throw new CacheException(String.format("Get from redis cache Error, for hash: %s", hash), e);
        }
    }
}