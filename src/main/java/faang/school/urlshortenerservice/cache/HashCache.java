package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.properties.HashCacheProperties;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    private final HashCacheProperties properties;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final TaskExecutor hashCacheExecutor;

    private BlockingQueue<String> cache;

    private final AtomicBoolean fillInProgress = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.cache = new LinkedBlockingQueue<>(properties.getMaxSize());
        log.info("HashCache initialized with maxSize={}", properties.getMaxSize());
    }

    public String getHash() {
        int currentSize = cache.size();
        int maxSize = properties.getMaxSize();
        int threshold = (int) (maxSize * (properties.getRefillThresholdPercent() / 100.0));

        if (currentSize < threshold) {
            maybeStartRefillAsync(currentSize);
        }

        String hash = cache.poll();
        log.debug("getHash() -> returning: {}", hash);
        return hash;
    }

    private void maybeStartRefillAsync(int currentSize) {
        if (fillInProgress.compareAndSet(false, true)) {
            log.info("Cache size {} < threshold, starting async refill", currentSize);
            hashCacheExecutor.execute(this::refillCache);
        }
    }

    private void refillCache() {
        try {
            int maxSize = properties.getMaxSize();
            int currentSize = cache.size();
            int spaceLeft = maxSize - currentSize;
            if (spaceLeft <= 0) {
                log.info("No space left in cache, skip refill");
                return;
            }

            List<String> dbHashes = hashRepository.getHashBatch(spaceLeft);
            log.info("Pulled {} hashes from DB to fill cache", dbHashes.size());

            for (String h : dbHashes) {
                boolean offered = cache.offer(h);
                if (!offered) {
                    log.warn("Cache is full, can't add more hashes");
                    break;
                }
            }
            log.info("Triggering hash generation in DB");
            hashGenerator.generateBatch();

        } catch (Exception e) {
            log.error("Error during refillCache()", e);
        } finally {
            fillInProgress.set(false);
        }
    }
}

