package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCache {
    private final RedisTemplate<String, String> redisTemplate;
    private final UrlRepository urlRepository;

    private static final String URL_KEY_PREFIX = "url:";
    private static final String COUNTER_PREFIX = "counter:";

    @Value("${url.cache.min-requests:10}")
    private int minRequestsForCaching;
    
    @Value("${url.cache.ttl-hours:24}")
    private int cacheTtlHours;

    public String getOriginalUrl(String hash) {
        String urlKey = URL_KEY_PREFIX + hash;
        String counterKey = COUNTER_PREFIX + hash;

        Long requestCount = redisTemplate.opsForValue().increment(counterKey);

        if (requestCount == null) {
            log.warn("Failed to increment counter for hash: {}", hash);
            return urlRepository.findByHash(hash)
                    .orElseThrow(() -> new EntityNotFoundException("Url not found"))
                    .getOriginalUrl();
        }

        if (requestCount == 1) {
            redisTemplate.expire(counterKey, cacheTtlHours, TimeUnit.HOURS);
        }

        String cachedUrl = redisTemplate.opsForValue().get(urlKey);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        String originalUrl = urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("Url not found"))
                .getOriginalUrl();

        if (requestCount >= minRequestsForCaching) {
            redisTemplate.opsForValue().set(urlKey, originalUrl, cacheTtlHours, TimeUnit.HOURS);
            log.info("URL with hash {} cached after {} requests", hash, requestCount);
        }

        return originalUrl;
    }
}
