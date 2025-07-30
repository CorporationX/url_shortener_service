package faang.school.urlshortenerservice.service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

import static faang.school.urlshortenerservice.utils.ConstantsUtilClass.NO_MAPPING_URL_FOR_HASH;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlRedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.default-ttl}")
    private long defaultTtlInSeconds;

    public void saveUrlMapping(String hash, String url) {
        saveUrlMapping(hash, url, defaultTtlInSeconds);
    }

    public void saveUrlMapping(String hash, String url, long ttlInSeconds) {
        redisTemplate.opsForValue().set(hash, url, Duration.ofSeconds(ttlInSeconds));
    }

    public Optional<String> getActualUrl(String hash) {
        String url = (String) redisTemplate.opsForValue().get(hash);
        if (url == null) {
            log.error(String.format(NO_MAPPING_URL_FOR_HASH, hash));
            return Optional.empty();
        }
        return Optional.of(url);
    }
}
