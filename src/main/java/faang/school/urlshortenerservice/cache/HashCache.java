package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashCacheRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashCacheRepository hashCacheRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Qualifier("hashCacheExecutor")
    private final ExecutorService executorService;

    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.refill-threshold}")
    private int refillThreshold;

    private static final String REDIS_HASH_CACHE_KEY = "hash_bucket";

    private final LinkedBlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    public String getHash() {

        String hash = hashQueue.poll();


        if (hash == null) {
            log.warn("In-memory hash queue is empty. Checking Redis cache...");
            hash = (String) redisTemplate.opsForHash().keys(REDIS_HASH_CACHE_KEY).stream()
                    .findFirst()
                    .orElse(null);

            if (hash != null) {

                redisTemplate.opsForHash().delete(REDIS_HASH_CACHE_KEY, hash);
                log.info("Retrieved hash '{}' from Redis cache.", hash);
            } else {
                log.error("No hash available in Redis. Triggering async refill...");
                triggerAsyncRefill();
                throw new IllegalStateException("Hash cache is empty! Refill in progress...");
            }
        }

        return hash;
    }

    private void triggerAsyncRefill() {
        if (refillInProgress.compareAndSet(false, true)) {
            log.info("Triggering async refill of the hash cache...");
            executorService.submit(() -> {
                try {
                    refillCache();
                } finally {
                    refillInProgress.set(false);
                }
            });
        } else {
            log.debug("Refill already in progress. Skipping duplicate trigger.");
        }
    }

    private void refillCache() {
        try {
            int currentCacheSize = hashQueue.size();
            int needed = maxSize - currentCacheSize;

            if (needed <= 0) {
                log.info("Hash cache is already full. No refill needed.");
                return;
            }

            log.info("Refilling hash cache with up to {} hashes...", needed);

            List<String> fetchedHashes = hashRepository.getHashBatch(needed);

            if (!fetchedHashes.isEmpty()) {
                hashQueue.addAll(fetchedHashes);
                for (String hash : fetchedHashes) {
                    redisTemplate.opsForHash().put(REDIS_HASH_CACHE_KEY, hash, true);
                }

                log.info("Refilled hash cache with {} hashes from the database.", fetchedHashes.size());
            }

            if (fetchedHashes.size() < needed) {
                int toGenerate = needed - fetchedHashes.size();
                log.info("Generating {} additional hashes.", toGenerate);
                hashGenerator.generatedBatch();
            }
        } catch (Exception e) {
            log.error("Error while refilling the hash cache: {}", e.getMessage(), e);
        }
    }
    public void addHash(String hash) {
        if (hashQueue.size() < maxSize) {
            hashQueue.offer(hash);
            redisTemplate.opsForHash().put(REDIS_HASH_CACHE_KEY, hash, true);
            log.info("Added hash '{}' to the cache.", hash);
        } else {
            log.warn("Cannot add hash '{}' to the cache. Cache is full.", hash);
        }
    }
    public int getQueueSize() {
        return hashQueue.size();
    }
    public int getRedisCacheSize() {
        Long size = redisTemplate.opsForHash().size(REDIS_HASH_CACHE_KEY);
        return size != null ? size.intValue() : 0;
    }
}