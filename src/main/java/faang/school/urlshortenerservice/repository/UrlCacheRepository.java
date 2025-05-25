package faang.school.urlshortenerservice.repository;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> hashRedisTemplate;
    @Value("${spring.data.redis.ttl}")
    private long timeToLiveInMinutes;

    @Nullable
    public String getUrl(String hash) {
        String url = null;
        try {
            url = hashRedisTemplate.opsForValue().get(hash);
            if (url != null) {
                hashRedisTemplate.expire(hash, timeToLiveInMinutes, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("Error while getting url", e);
        }
        log.debug("Url for hash: {} is {}", hash, url);
        return url;
    }

    public void setUrl(String hash, String url) {
        try {
            hashRedisTemplate.opsForValue().set(hash, url, timeToLiveInMinutes, TimeUnit.MINUTES);
            log.debug("Add url {} to hash {} on redis", url, hash);
        } catch (Exception e) {
            log.error("Error while add url {} to hash {} on redis", url, hash, e);
        }
    }

    public void removeUrl(String hash) {
        try {
            hashRedisTemplate.delete(hash);
            log.debug("Hash {} removed from redis", hash);
        } catch (Exception e) {
            log.error("Error while removing url {}", hash, e);
        }
    }
}
