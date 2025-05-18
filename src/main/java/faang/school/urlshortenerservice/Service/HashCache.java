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

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final CacheProperties cacheProperties;
    private final ExecutorService executorService;
    private final HashProperties hashProperties;

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    public String getHash() {
        int currentSize = cache.size();
        int threshold = (cacheProperties.maxSize() * cacheProperties.refillPercent()) / 100;

        if (currentSize > threshold) {
            return cache.poll();
        } else {
            refillTrigger();
            return cache.poll(); // всё равно пытаемся вернуть, что есть
        }
    }

    private void refillTrigger() {
        if (refillInProgress.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
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
