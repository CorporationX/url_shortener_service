package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${local-cache.capacity}")
    private int capacity;

    @Value("${local-cache.capacity-usage}")
    private int capacityUsage;

    @Value("${hash.constants.batch-size}")
    private int batchSize;

    private final HashGenerator hashGenerator;
    private final HashService hashService;
    private final Queue<String> hashesCache = new ArrayBlockingQueue<>(capacity);
    private final AtomicBoolean isFillingRequired = new AtomicBoolean(false);

    @PostConstruct
    public void warmCache() {
        hashGenerator.generateBatch(batchSize);
        CompletableFuture<List<String>> future = hashService.getHashes(capacity);
        List<String> hashes = future.join();
        hashesCache.addAll(hashes);
    }

    public String getHash() {
        double currentCapacity = (hashesCache.size() / (double) capacity) * 100.0;
        if (currentCapacity > capacityUsage) {
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
