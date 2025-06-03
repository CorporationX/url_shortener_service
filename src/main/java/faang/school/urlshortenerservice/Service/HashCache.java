package faang.school.urlshortenerservice.Service;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {
    private static int RETRIES = 20;

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final CacheProperties cacheProperties;
    private final ExecutorService executorService;
    private final HashProperties hashProperties;

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    public String getHash() {
        int threshold = (cacheProperties.maxSize() * cacheProperties.refillPercent()) / 100;
        String hash = cache.poll();
        if (hash != null) {

            if (cache.size() <= threshold) {
                refillTrigger();
            }
            return hash;
        }
        refillTrigger();
        while (RETRIES-- > 0) {
            hash = cache.poll();
            if (hash != null) {
                return hash;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for hash refill");
            }
        }
        throw new IllegalStateException("HashCache timeout: no hashes available after async refill");
    }

    private void refillTrigger() {
        if (refillInProgress.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    hashGenerator.generateBatch();
                    refillCache();
                    executorService.submit(hashGenerator::generateBatch);
                } catch (Exception e) {
                    log.error("Error from cache generator", e);
                } finally {
                    refillInProgress.set(false);
                }
            });
        }
    }

    private void refillCache() {
        List<String> hashes = hashRepository.getHashBatch(hashProperties.batchsize());
        for (String hash : hashes) {
            cache.offer(hash);
        }
        log.info("Hash cache size was upload : {}", cache.size());
    }
}
