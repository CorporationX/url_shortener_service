package faang.school.urlshortenerservice.repository.impl;

import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UrlCacheRepositoryImpl implements UrlCacheRepository {
    private static final String HASH_TO_URL = "HASH_TO_URL";
    private static final String URL_TO_HASH = "URL_TO_HASH";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Object> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void add(String url, String hash) {
        hashOperations.put(HASH_TO_URL, hash, url);
        hashOperations.put(URL_TO_HASH, url, hash);
        log.info("Added hash: {} and url: {} into Redis", hash, url);
    }

    @Override
    public String getUrl(String hash) {
        String url = (String) hashOperations.get(HASH_TO_URL, hash);
        if (url != null) {
            log.info("Found url: {} for hash: {}", url, hash);
        } else {
            log.warn("No url found for hash: {}", hash);
        }
        return url;
    }

    @Override
    public String getHash(String url) {
        String hash = (String) hashOperations.get(URL_TO_HASH, url);
        if (hash != null) {
            log.info("Found hash: {} for url: {}", hash, url);
        } else {
            log.warn("No hash found for url: {}", url);
        }
        return hash;
    }

}
