package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.annotation.RefillsHashPool;
import faang.school.urlshortenerservice.dto.HashPoolStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisShortenerRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${shortener.redis-short-url-prefix}")
    private String shortUrlPrefix;

    @Value("${shortener.redis-free-hash-prefix}")
    private String freeHashPrefix;

    @Value("${shortener.max-url-redis-ttl-minutes}")
    private int maxRedisTtlMinutes;

    @Value("${shortener.redis-refill-hash-lock-seconds}")
    private int refillLockSeconds;

    public void saveShortUrl(String hash, String longUrl, long requestedTtlMinutes) {
        long ttl = Math.min(requestedTtlMinutes, maxRedisTtlMinutes);

        redisTemplate.opsForHash().put(shortUrlPrefix, hash, longUrl);
        redisTemplate.expire(shortUrlPrefix, ttl, TimeUnit.MINUTES);
    }

    public String getLongUrl(String hash) {
        return (String) redisTemplate.opsForHash().get(shortUrlPrefix, hash);
    }

    public String getFreeHash() {
        return redisTemplate.opsForList().leftPop(freeHashPrefix);
    }

    public long getFreeHashListSize() {
        Long size = redisTemplate.opsForList().size(freeHashPrefix);
        return size == null ? 0 : size;
    }

    public boolean tryLockForGeneration() {
        Boolean b = redisTemplate.opsForValue().setIfAbsent("hashGenerationLock", "LOCKED", refillLockSeconds, TimeUnit.SECONDS);
        return b == null ? false : b;
    }

    public void releaseLock() {
        redisTemplate.delete("hashGenerationLock");
    }

    public void saveFreeHashesBatch(List<String> hashes) {
        if (!hashes.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(freeHashPrefix, hashes);
        }
    }

    @RefillsHashPool
    public HashPoolStatus getFreeHashWithStatus() {
        String hash = getFreeHash();
        Long remaining = redisTemplate.opsForList().size(freeHashPrefix);
        return new HashPoolStatus(hash, remaining);
    }
}
