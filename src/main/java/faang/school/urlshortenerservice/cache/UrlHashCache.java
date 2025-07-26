package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.cassandra.UrlHashRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlHashCache {

    private static final String URL_MAPPINGS_COUNT_KEY = "url_mappings_count";

    private final UrlHashRepository urlHashRepository;
    @Qualifier("urlHashCacheRedisTemplate")
    private final RedisTemplate<String, String> urlHashCacheRedisTemplate;

    public long getSize() {
        String countStr = urlHashCacheRedisTemplate.opsForValue().get(URL_MAPPINGS_COUNT_KEY);
        long size = (countStr != null) ? Long.parseLong(countStr) : 0L;
        log.info("Current URL mappings cache size: {}", size);
        return size;
    }

    public void put(String hash, String fullUrl, long ttlSeconds) {
        ValueOperations<String, String> valueOps = urlHashCacheRedisTemplate.opsForValue();

        Boolean isNewKey = valueOps.setIfAbsent(hash, fullUrl, Duration.ofSeconds(ttlSeconds)); // сюда брать из properties.ttl? или настройка в application.yaml?
        log.info("DEBUG: Using RedisTemplate with ConnectionFactory type: {}", urlHashCacheRedisTemplate.getConnectionFactory().getClass().getName());
        log.info("DEBUG: Current ConnectionFactory host: {}, port: {}",
                ((LettuceConnectionFactory) urlHashCacheRedisTemplate.getConnectionFactory()).getHostName(),
                ((LettuceConnectionFactory) urlHashCacheRedisTemplate.getConnectionFactory()).getPort());
        if (Boolean.TRUE.equals(isNewKey)) {
            valueOps.increment(URL_MAPPINGS_COUNT_KEY);
            log.info("Put NEW hash: {} with fullUrl: {} into URL mappings cache with TTL: {}s. Count incremented.",
                    hash, fullUrl, ttlSeconds);
            Long ttlCheck = urlHashCacheRedisTemplate.getExpire(hash);
            String valueCheck = valueOps.get(hash);
            log.info("DEBUG CHECK: After put, key '{}' has TTL: {}s, Value: '{}'", hash, ttlCheck, valueCheck);
        }
    }

    public String get(String hash) {
        String fullUrl = urlHashCacheRedisTemplate.opsForValue().get(hash);
        if (fullUrl != null) {
            log.info("Retrieved fullUrl: {} for hash: {} from URL mappings cache.", fullUrl, hash);
            Long ttlCheck = urlHashCacheRedisTemplate.getExpire(hash);
            String valueCheck = urlHashCacheRedisTemplate.opsForValue().get(hash);
            log.info("DEBUG CHECK: After get, key '{}' has TTL: {}s, Value: '{}'", hash, ttlCheck, valueCheck);
        } else {
            log.info("Hash: {} not found in URL mappings cache (might have expired).", hash);
            // Если не в кэше, ищем в Cassandra
            fullUrl = urlHashRepository.findByHash(hash);
            if (fullUrl == null || fullUrl.isEmpty()) {
                throw new EntityNotFoundException(String.format("Short URL %s not found in Cassandra either.", fullUrl));
            }
            // Кладем найденный URL в кэш для будущих запросов (с TTL)
            put(hash, fullUrl, 120); // TTL 120 секунд
        }
        return fullUrl;
    }
}