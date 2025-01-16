package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Qualifier("hashCacheExecutor")
    private final ExecutorService executorService;

    private final ConcurrentLinkedDeque<String> hashQueue = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.refill-threshold}")
    private int refillThreshold;

    public String getHash() {
        if (hashQueue.size() < (maxSize * refillThreshold / 100)) {
            triggerAsyncRefill();
        }

        String hash = hashQueue.poll();
        if (hash == null) {
            log.warn("Cache is empty! Unable to retrieve a hash.");
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
            int needed = maxSize - hashQueue.size();
            if (needed <= 0) {
                log.info("Hash cache is already full. No refill needed.");
                return;
            }

            List<String> hashes = hashRepository.getHashBatch(needed);
            log.info("Fetched {} hashes from the database.", hashes.size());
            hashQueue.addAll(hashes);

            if (hashes.size() < needed) {
                log.info("Generating additional hashes to meet the cache size.");
                hashGenerator.generatedBatch();
            }
        } catch (Exception e) {
            log.error("Error while refilling the hash cache: {}", e.getMessage(), e);
        } finally {
            refillInProgress.set(false);
        }
    }
}