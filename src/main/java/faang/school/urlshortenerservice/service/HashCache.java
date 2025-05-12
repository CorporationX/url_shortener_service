package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.component.HashGenerator;
import faang.school.urlshortenerservice.config.app.HashCacheConfig;
import faang.school.urlshortenerservice.config.app.HashGeneratorConfig;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    private final HashCacheConfig config;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutor;

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private final HashGeneratorConfig hashGeneratorConfig;

    public String getHash() {
        String hash = cache.poll();
        if (hash == null) {
            log.warn("Cache is empty, returning null");
            return null;
        }
        log.debug("Returning hash: {}", hash);

        int currentSize = cache.size();
        int maxSize = config.getMaxSize();
        int threshold = (int) (maxSize * (config.getRefillThreshold() / 100.0));
        if (currentSize < threshold && isRefilling.compareAndSet(false, true)) {
            log.info("Cache size {}/{} is below threshold ({}), starting async refill",
                    currentSize, maxSize, threshold);
            hashCacheExecutor.submit(this::refillCache);
        }

        return hash;
    }

    private void refillCache() {
        try {
            int maxSize = config.getMaxSize();
            int toFetch = maxSize - cache.size();
            if (toFetch <= 0) {
                log.info("Cache already full, skipping refill");
                return;
            }

            log.info("Fetching {} hashes from repository", toFetch);
            List<String> newHashes = hashRepository.getHashBatch();
            cache.addAll(newHashes);
            log.info("Added {} hashes to cache, current size: {}", newHashes.size(), cache.size());

            int batchSize = hashGeneratorConfig.getBatchSize();
            int batches = (int) Math.ceil((double) toFetch / batchSize);
            for (int i = 0; i < batches; i++) {
                log.info("Triggering batch {} of {} for async generation", i + 1, batches);
                hashGenerator.generateBatch();
            }
            log.info("Triggered {} batches for total ~{} hashes", batches, toFetch);
        } catch (Exception e) {
            log.error("Error refilling cache", e);
        } finally {
            isRefilling.set(false);
            log.info("Refill completed, isRefilling reset to false");
        }
    }
}
