package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheThreadPool;

    private BlockingQueue<String> hashQueue;
    private volatile boolean refillInProgress = false;
    private final Object lock = new Object();

    @Value("${hash.cache.size}")
    private int cacheSize;
    @Value("${hash.cache.refill.threshold}")
    private double refillThreshold;

    @PostConstruct
    public void init() {
        hashQueue = new LinkedBlockingQueue<>();
        refillCache();
    }

    @PreDestroy
    public void shutdown() {
        hashCacheThreadPool.shutdown();
        try {
            if (!hashCacheThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                hashCacheThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            hashCacheThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public String getHash() {
        String hash = hashQueue.poll();
        if(shouldRefill()) {
            synchronized (lock) {
                if (shouldRefill() && !refillInProgress) {
                    refillInProgress = true;
                    refillCache();
                }
            }
        }
        return hash;
    }

    private boolean shouldRefill() {
        return hashQueue.size() < cacheSize * refillThreshold;
    }

    @Async("hashCacheThreadPool")
    private void refillCache() {
        try {
            int batchSize = cacheSize - hashQueue.size();
            log.debug("Refilling hash cache with {} hashes", batchSize);
            List<String> newHashes = hashRepository.getHashBatch(batchSize);

            hashQueue.addAll(newHashes);

            if (newHashes.size() < batchSize) {
                log.info("Requested {} hashes but got only {}, triggering generation", batchSize, newHashes.size());
                hashGenerator.generateBatch();
            }
        } catch (Exception e) {
            log.error("Error refilling hash cache", e);
            throw new HashGenerationException(e.getMessage());
        } finally {
            refillInProgress = false;
        }
    }
}
