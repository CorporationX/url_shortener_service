package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.redis.url_hash_cache.RedisUrlHashCacheProperties;
import faang.school.urlshortenerservice.exceptions.NonExistingHashProvided;
import faang.school.urlshortenerservice.repository.cassandra.UrlHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlHashCache {

    private final UrlHashRepository urlHashRepository;
    private final RedisUrlHashCacheProperties properties;
    private final RedisTemplate<String, String> urlHashCacheRedisTemplate;

    public void put(String hash, String fullUrl) {
        urlHashCacheRedisTemplate.opsForValue().setIfAbsent(hash, fullUrl, Duration.ofSeconds(properties.getTtl()));
    }

    public String get(String hash) {
        String fullUrl = urlHashCacheRedisTemplate.opsForValue().get(hash);

        if (fullUrl != null) {
            log.info("Retrieved fullUrl: {} for hash: {} from URL mappings cache.", fullUrl, hash);
        } else {
            fullUrl = urlHashRepository.findByHash(hash).orElseThrow(() -> new NonExistingHashProvided(
                    String.format("Full url for hash: %s not found in both Cache/DB.", hash)));

            put(hash, fullUrl);
        }

        return fullUrl;
    }
}