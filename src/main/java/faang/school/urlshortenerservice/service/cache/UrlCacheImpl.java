package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlCacheImpl implements UrlCache {
    private static final String PREFIX = "urls:";

    private final StringRedisTemplate cache;
    private final UrlRepository urlRepository;
    @Value("${url.cache.ttl}")
    private int ttl;

    String getKey(String hash) {
        return PREFIX + hash;
    }

    @Override
    public String get(String hash) {
        try {
            String url = cache.opsForValue().get(getKey(hash));
            if (url == null || url.isBlank()) {
                try {
                    url = urlRepository.findByIdOrThrow(hash).getUrl();
                    set(hash, url);
                } catch (Exception e) {
                    log.error("Error when getting the URL for the hash {}", hash, e);
                    throw new ResourceNotFoundException("URL не найден для хеша: " + hash);
                }
            }
            return url;
        } catch (Exception e) {
            log.error("An error occurred while receiving data for the hash {}", hash, e);
            throw new RuntimeException("Request processing error", e);
        }
    }

    @Override
    public void set(String hash, String url) {
        try {
            log.debug("Setting cache entry for hash: {}, URL: {}", hash, url);
            cache.opsForValue().set(getKey(hash), url, ttl, TimeUnit.SECONDS);
            log.debug("Successfully set cache entry for hash: {}", hash);
        } catch (Exception e) {
            log.error("Failed to set cache entry for hash: {}", hash, e);
            throw new RuntimeException("Error setting cache entry", e);
        }
    }

    @Override
    public void delete(String hash) {
        try {
            log.debug("Deleting cache entry for hash: {}", hash);
            cache.opsForValue().getOperations().delete(getKey(hash));
            log.debug("Successfully deleted cache entry for hash: {}", hash);
        } catch (Exception e) {
            log.error("Failed to delete cache entry for hash: {}", hash, e);
            throw new RuntimeException("Error deleting cache entry", e);
        }
    }

    @Override
    public void deleteAll(List<String> hashes) {
        try {
            log.debug("Deleting multiple cache entries for hashes: {}", hashes);
            cache.opsForValue()
                    .getOperations()
                    .delete(hashes.stream()
                            .map(this::getKey)
                            .toList());
            log.debug("Successfully deleted {} cache entries", hashes.size());
        } catch (Exception e) {
            log.error("Failed to delete multiple cache entries", e);
            throw new RuntimeException("Error deleting multiple cache entries", e);
        }
    }
}