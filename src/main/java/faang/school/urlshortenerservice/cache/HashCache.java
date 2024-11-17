package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${local.cache.capacity}")
    private int capacity;

    @Value("${local.cache.capacity-usage}")
    private int capacityUsage;

    @Value("${hash.constants.batch.size}")
    private int batchSize;

    private int threshold;

    private final HashGenerator hashGenerator;
    private final HashService hashService;
    private Queue<String> hashesCache;
    private AtomicBoolean isFillingRequired;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        hashesCache = new ArrayBlockingQueue<>(capacity);
        isFillingRequired = new AtomicBoolean(false);
        threshold = (int) ((capacity * capacityUsage) / 100.0);
        warmCache();
    }

    private void warmCache() {
        hashGenerator.generateBatch(batchSize).join();
        CompletableFuture<List<String>> future = hashService.getHashes(capacity);
        List<String> hashes = future.join();
        hashesCache.addAll(hashes);
        log.info("Warming up cache for {} hashes", hashes.size());
    }

    public String getHash() {
        if (hashesCache.size() < threshold) {
            isFillingRequired.set(true);
            if (isFillingRequired.get()) {
                hashGenerator.generateBatch(capacity - hashesCache.size());
                hashService.getHashes(capacity - hashesCache.size())
                        .thenAccept(hashesCache::addAll)
                        .thenRun(() -> isFillingRequired.set(false));
            }
        }
        return hashesCache.poll();
    }
}
