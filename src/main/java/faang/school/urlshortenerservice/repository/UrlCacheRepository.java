package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UrlCacheRepository {


    private final StringRedisTemplate stringRedisTemplate;
    @Value("${spring.data.redis.TTL-HOURS}")
    private long TTL_HOURS;

    public void set(String hash, String url) {
        log.debug("Setting URL for hash: {}", hash);
        stringRedisTemplate.opsForValue().set(hash, url, Duration.ofHours(TTL_HOURS));
    }

    public Optional<String> get(String hash) {
        log.debug("Getting URL for hash: {}", hash);
        Optional<String> url = Optional.ofNullable(stringRedisTemplate.opsForValue().get(hash));
        if (url.isPresent()) {
            log.debug("Found URL: {}", url.get());
            stringRedisTemplate.expire(hash, Duration.ofHours(TTL_HOURS));
        }
        return url;
    }
}