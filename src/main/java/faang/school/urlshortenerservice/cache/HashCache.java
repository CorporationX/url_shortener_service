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
            log.warn("Cache is empty! Returning null.");
        }
        return hash;
    }

    private void triggerAsyncRefill() {
        if (refillInProgress.compareAndSet(false, true)) {
            log.info("Triggering async refill of the hash cache...");

            executorService.submit(() -> {
                try {

                    List<String> hashes = hashRepository.getHashBatch(maxSize - hashQueue.size());
                    log.info("Fetched {} hashes from the database.", hashes.size());
                    hashQueue.addAll(hashes);

                    log.info("Triggering hash generation in the database.");
                    hashGenerator.generatedBatch();
                } catch (Exception e) {
                    log.error("Error while refilling the hash cache: {}", e.getMessage(), e);
                } finally {
                    refillInProgress.set(false);
                }
            });
        } else {
            log.info("Refill already in progress. Skipping duplicate trigger.");
        }
    }
}


