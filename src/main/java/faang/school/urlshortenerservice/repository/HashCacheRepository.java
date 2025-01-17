package faang.school.urlshortenerservice.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    public static final String HASH_BUCKET = "hash_bucket";
    private static final String HASH_CACHE = "hash_cache";

    public void saveHashes(List<String> hashes) {
        hashes.forEach(this::saveHash);
    }

    @CachePut(cacheNames = HASH_CACHE, key = "#hash")
    public void saveHash(String hash) {
        redisTemplate.opsForHash().put(HASH_BUCKET, hash, true);
    }

    public String getAndRemoveHash() {
        String hash = (String) redisTemplate.opsForHash().keys(HASH_BUCKET).stream().findFirst().orElse(null);
        if (hash != null) {
            redisTemplate.opsForHash().delete(HASH_BUCKET, hash);
        }
        return hash;
    }

    @Cacheable(cacheNames = HASH_CACHE, key = "#hash")
    public boolean exists(String hash) {
        return redisTemplate.opsForHash().hasKey(HASH_BUCKET, hash);
    }

    public int size() {
        Long size = redisTemplate.opsForHash().size(HASH_BUCKET);
        return size != null ? size.intValue() : 0;
    }
}