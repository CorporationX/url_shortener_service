package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class UrlCacheRepository {
    @Cacheable(value = "url", key = "#hash", cacheManager = "cacheManager")
    public URL saveAtRedis(String hash, URL url) {
        return url;
    }

    @CacheEvict(value = "url", key = "#hash", cacheManager = "cacheManager")
    public void deleteFormRedis(String hash) {
    }
}
