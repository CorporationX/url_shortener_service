package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Qualifier("hashCacheExecutor")
    private final ExecutorService executorService;

    @Value("${app.hash.cache.size}")
    private int cacheSize;

    @Value("${app.hash.cache.refill-threshold-percent}")
    private int refillThresholdPercent;

    @Value("${app.hash.batch-size}")
    private int batchSize;

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    public String getHash() {
        if (shouldRefill()) {
            refillCache();
        }

        try {
            return hashQueue.poll();
        } catch (Exception e) {
            log.error("Error getting hash from cache", e);
            return null;
        }
    }

    private boolean shouldRefill() {
        int threshold = (cacheSize * refillThresholdPercent) / 100;
        return hashQueue.size() < threshold;
    }

    private void refillCache() {
        if (isRefilling.compareAndSet(false, true)) {
            executorService.submit(() -> {
                try {
                    log.info("Starting cache refill");
                    List<String> hashes = hashRepository.getHashBatch(batchSize);
                    hashQueue.addAll(hashes);
                    log.info("Added {} hashes to cache", hashes.size());

                    // Асинхронно запускаем генерацию новых хешей
                    hashGenerator.generateBatch();
                } catch (Exception e) {
                    log.error("Error refilling cache", e);
                } finally {
                    isRefilling.set(false);
                }
            });
        }
    }
}
