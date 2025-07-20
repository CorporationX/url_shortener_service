package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.properties.CacheConfig;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final CacheConfig cacheConfig;
    private final HashGenerator hashGenerator;

    private Queue<String> hashQueue;
    private AtomicBoolean isGeneratingHashes;

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(cacheConfig.getCapacity());
        generateAndFillCacheAsync()
                .join();
    }

    public String getHash() {
        if ((hashQueue.size() / cacheConfig.getCapacity()) * 100 < cacheConfig.getFillPercentage()) {
            if (isGeneratingHashes.compareAndSet(false, true)) {
                generateAndFillCacheAsync()
                        .thenRun(() -> isGeneratingHashes.set(false));
            }
        }
        return hashQueue.poll();
    }

    private CompletableFuture<Void> generateAndFillCacheAsync() {
        return hashGenerator.generateBatch()
                .thenApply(list -> list.stream()
                        .map(Hash::getHash)
                        .collect(Collectors.toList()))
                .thenAccept(hashQueue::addAll);
    }
}
