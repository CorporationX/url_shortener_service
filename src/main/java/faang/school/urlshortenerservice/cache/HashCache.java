package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@EnableAsync
@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.size:100}")
    private int maxCacheSize;

    @Value("${hash.cache.threshold:10}")
    private double threshold;

    private final BlockingQueue<String> cache = new LinkedBlockingQueue<>();
    private final AtomicBoolean isLoading = new AtomicBoolean(false);

    @Transactional
    public String getHash() throws InterruptedException {
        if (cache.size() <= maxCacheSize * threshold && isLoading.compareAndSet(false, true)) {
            log.info("Cache size below threshold (current: {}, threshold: {}), triggering async refill",
                    cache.size(), maxCacheSize * threshold);
            refillCacheAsync();
        }

        String hash = cache.take();
        log.debug("Hash retrieved from cache: {}", hash);
        return hash;
    }

    @Async
    protected void refillCacheAsync() {
        try {
            log.info("Refilling cache. Requesting {} hashes from repository", maxCacheSize - cache.size());
            List<String> newHashes = hashGenerator.getHashes(maxCacheSize - cache.size());

            newHashes.forEach(hash -> {
                cache.add(hash);
                log.trace("Added hash to cache: {}", hash);
            });

            log.info("Cache refilled with {} hashes. Current cache size: {}",
                    newHashes.size(), cache.size());
        } catch (Exception e) {
            log.error("Cache refill failed", e);
        } finally {
            isLoading.set(false);
        }
    }
}