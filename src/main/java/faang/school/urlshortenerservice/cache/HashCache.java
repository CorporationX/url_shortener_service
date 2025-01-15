package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final List<String> cache = new CopyOnWriteArrayList<>();
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final CountDownLatch cacheFillingLatch = new CountDownLatch(1);

    private final int maxSize = 10000;
    private final int thresholdPercentage = 20;
    private final AtomicBoolean isFetching = new AtomicBoolean(false);

    public String getHash() {
        if (cache.size() > maxSize * thresholdPercentage / 100) {
            return getAndRemoveHash(cache);
        }

        if (isFetching.compareAndSet(false, true)) {
            executorService.submit(this::fillCacheAsync);
        }

        try {
            cacheFillingLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error while waiting for cache filling", e);
            return null;
        }

        return getAndRemoveHash(cache);
    }

    private String getAndRemoveHash(List<String> cache) {
        String hash = cache.get(0);
        cache.remove(0);

        HashEntity hashEntity = hashRepository.findByHash(hash);
        hashEntity.setIsUsed(true);
        hashRepository.save(hashEntity);

        log.info("Hash removed from cache: {}", hash);
        log.info("Cache size: {}", cache.size());
        return hash;
    }

    private void fillCacheAsync() {
        try {
            log.info("Adding hashes to cache...");
            List<String> hashes = hashRepository.getAvailableHashes();

            cache.addAll(hashes);

            if (cache.size() < maxSize) {
                List<String> newHashes = hashGenerator.generateBatch(maxSize - cache.size());
                cache.addAll(newHashes);
            }

            log.info("Cache size: {}", cache.size());
        } catch (Exception e) {
            log.error("Error while adding hashes to cache", e);
        } finally {
            cacheFillingLatch.countDown();
            isFetching.set(false);
        }
    }
}
