package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UrlCacheRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final UrlRepository urlRepository;

    public String findUrl(String hash) {
        log.debug("Searching for URL by hash: {}", hash);

        String cachedUrl = redisTemplate.opsForValue().get(hash);

        if (cachedUrl == null) {
            log.info("URL not found in cache for hash: {}. Searching in the database.", hash);
            try {
                Url url = urlRepository.findById(hash)
                        .orElseThrow(() -> {
                            log.warn("URL with hash: {} not found in the database.", hash);
                            return new EntityNotFoundException("URL not found.");
                        });
                cachedUrl = url.getUrl();
                log.debug("URL found in the database for hash: {}", hash);

                redisTemplate.opsForValue().set(hash, cachedUrl);
                log.info("URL for hash: {} added to the cache.", hash);
            } catch (EntityNotFoundException e) {
                log.error("Error while searching for URL by hash: {}", hash, e);
                throw e;
            }
        } else {
            log.debug("URL found in cache for hash: {}", hash);
        }
        return cachedUrl;
    }
}
