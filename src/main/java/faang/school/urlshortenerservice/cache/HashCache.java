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

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);


    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.refill-threshold}")
    private int refillThreshold;

    public String getHash() {
        String hash = hashCacheRepository.getAndRemoveHash();

        if (hash == null) {
            log.warn("Hash cache is empty! Triggering async refill.");
            triggerAsyncRefill();
            throw new IllegalStateException("Hash cache is empty!");
        }
        return hash;
    }

    private void triggerAsyncRefill() {
        if (refillInProgress.compareAndSet(false, true)) {
            log.info("Triggering async refill of the hash cache...");
            executorService.submit(this::refillCache);
        } else {
            log.debug("Refill already in progress. Skipping duplicate trigger.");
        }
    }

    private void refillCache() {
        try {
            int currentCacheSize = hashCacheRepository.size();
            int needed = maxSize - currentCacheSize;

            if (needed <= 0) {
                log.info("Hash cache is already full. No refill needed.");
                return;
            }

            List<String> hashes = hashRepository.getHashBatch(needed);
            log.info("Fetched {} hashes from the database.", hashes.size());

            hashCacheRepository.saveHashes(hashes);

            if (hashes.size() < needed) {
                int toGenerate = needed - hashes.size();
                log.info("Generating {} additional hashes.", toGenerate);
                hashGenerator.generatedBatch();
            }
        } catch (Exception e) {
            log.error("Error while refilling the hash cache: {}", e.getMessage(), e);
        } finally {
            refillInProgress.set(false);
        }
    }
}