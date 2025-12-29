package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.RetryExecutor;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Component
@ConditionalOnProperty(name = "hash.cache.type", havingValue = "in-memory", matchIfMissing = false)
public class HashCacheServiceImp implements HashCacheService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final Base62Encoder base62Encoder;
    private final ExecutorService executorService;
    private final RetryExecutor retryExecutor;

    @Value("${hash.cache.max-size:1000}")
    private int maxCacheSize;

    @Value("${hash.cache.refill-threshold-percent:20}")
    private int refillThresholdPercent;

    private final ConcurrentLinkedDeque<String> cache = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);
    private final int refillThreshold;

    public HashCacheServiceImp(
            HashRepository hashRepository,
            HashGenerator hashGenerator,
            Base62Encoder base62Encoder,
            @Qualifier("hashCacheExecutor") ExecutorService executorService,
            RetryExecutor retryExecutor,
            @Value("${hash.cache.max-size:1000}") int maxCacheSize,
            @Value("${hash.cache.refill-threshold-percent:20}") int refillThresholdPercent) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.base62Encoder = base62Encoder;
        this.executorService = executorService;
        this.retryExecutor = retryExecutor;
        this.maxCacheSize = maxCacheSize;
        this.refillThresholdPercent = refillThresholdPercent;
        this.refillThreshold = (maxCacheSize * refillThresholdPercent) / 100;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing HashCache (maxSize={}, threshold={})", maxCacheSize, refillThreshold);
        
        try {
            refillCache();
            
            if (cache.isEmpty()) {
                log.warn("HashCache initialized empty! Database may be empty.");
            } else {
                log.info("HashCache initialized with {} hashes", cache.size());
            }
        } catch (Exception e) {
            log.error("Failed to initialize HashCache", e);
        }
    }

    @Override
    public String getHash() {
        String hash = cache.pollFirst();
        
        if (hash != null) {
            int currentSize = cache.size();
            if (currentSize <= refillThreshold) {
                triggerRefill();
            }
            return hash;
        }
        
        triggerRefill();
        return getHashFallback();
    }

    @Override
    public void returnHash(String hash) {
        if (hash != null && !hash.isEmpty()) {
            if (cache.size() < maxCacheSize) {
                cache.addFirst(hash);
                log.debug("Returned hash to cache: {}", hash);
            } else {
                log.debug("Cache is full, hash not returned: {}", hash);
            }
        }
    }

    @Override
    public int size() {
        return cache.size();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down HashCache executor service");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, SECONDS)) {
                log.warn("ExecutorService did not terminate gracefully, forcing shutdown");
                List<Runnable> pendingTasks = executorService.shutdownNow();
                if (!pendingTasks.isEmpty()) {
                    log.warn("Cancelled {} pending tasks", pendingTasks.size());
                }
                if (!executorService.awaitTermination(10, SECONDS)) {
                    log.error("ExecutorService did not terminate after forced shutdown");
                }
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while shutting down executor service", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private String getHashFallback() {
        try {
            List<String> hashes = retryExecutor.execute(() -> 
                hashRepository.getHashBatch()
            );

            if (hashes != null && !hashes.isEmpty()) {
                String hash = hashes.get(0);
                if (hashes.size() > 1) {
                    int added = 0;
                    for (int i = 1; i < hashes.size() && cache.size() < maxCacheSize; i++) {
                        cache.add(hashes.get(i));
                        added++;
                    }
                    if (added > 0) {
                        log.debug("Fallback: added {} hashes to cache", added);
                    }
                }
                log.debug("Fallback: retrieved hash from database");
                return hash;
            }
        } catch (Exception e) {
            log.error("Fallback: failed to get hash from database", e);
        }

        log.warn("Fallback: generating hash on-the-fly (last resort)");
        return generateHashImmediately();
    }

    private String generateHashImmediately() {
        try {
            List<Long> numbers = retryExecutor.execute(() -> 
                hashRepository.getUniqueNumbers(1)
            );

            if (numbers == null || numbers.isEmpty()) {
                throw new RuntimeException("Sequence exhausted");
            }

            String hash = base62Encoder.encode(numbers.get(0));

            retryExecutor.execute(() -> {
                hashRepository.save(List.of(hash));
                return null;
            });

            log.info("Generated hash immediately: {}", hash);
            return hash;

        } catch (Exception e) {
            log.error("CRITICAL: Cannot generate hash!", e);
            throw new RuntimeException("Hash generation failed", e);
        }
    }

    private void triggerRefill() {
        if (refillInProgress.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    while (cache.size() < refillThreshold) {
                        refillCache();

                        if (cache.isEmpty()) {
                            log.warn("Refill produced no hashes, stopping");
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.error("Refill failed", e);
                } finally {
                    refillInProgress.set(false);
                }
            });
        }
    }

    private void refillCache() {
        try {
            List<String> hashes = retryExecutor.execute(() -> 
                hashRepository.getHashBatch()
            );

            if (hashes == null || hashes.isEmpty()) {
                log.warn("No hashes available in database for cache refill");
                return;
            }

            int added = 0;
            for (String hash : hashes) {
                if (cache.size() >= maxCacheSize) {
                    continue;
                }
                cache.add(hash);
                added++;
            }

            if (added > 0) {
                log.info("Cache refilled with {} hashes. Current cache size: {}", added, cache.size());
                hashGenerator.generateHashBatch();
            } else {
                log.debug("Cache is full, no hashes added");
            }

        } catch (Exception e) {
            log.error("Error during cache refill", e);
        }
    }
}

