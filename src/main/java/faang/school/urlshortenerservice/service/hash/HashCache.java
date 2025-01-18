package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final TaskExecutor threadPool;
    private final ArrayBlockingQueue<String> cache;
    private final AtomicBoolean canGenerateMoreHashes = new AtomicBoolean(true);

    @Value("${spring.cache.redis.threshold}")
    private float threshold;

    @Value("${spring.cache.redis.size}")
    private int cacheSize;

    public String getHash() {
        log.info("Trying to get random hash");
        ensureCacheCapacityIsMaintained();
        return cache.poll();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fillCacheOnBoot() {
        log.info("Trying to fill cache on boot");
        CompletableFuture<Void> future = hashGenerator.generateBatch();
        future.join();
        List<String> hashes = hashRepository.getHashBatch(cacheSize);
        cache.addAll(hashes);
    }

    private void ensureCacheCapacityIsMaintained() {
        log.info("Checking whether capacity is maintained");
        if (isCacheSizeUnderThreshold() && canGenerateMoreHashes.compareAndSet(true, false)) {
            log.info("Amount of hashes available in cache is under the threshold. Refilling...");
            long refillBatchSize = (long) (cacheSize * (1 - threshold));
            List<String> hashes = hashRepository.getHashBatch(refillBatchSize);
            threadPool.execute(() -> {
                cache.addAll(hashes);
                canGenerateMoreHashes.set(true);
            });
            hashGenerator.generateBatch();
        }
    }

    private boolean isCacheSizeUnderThreshold() {
        return (float) cache.size() / cacheSize < threshold;
    }
}
