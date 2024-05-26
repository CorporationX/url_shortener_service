package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.hash.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${cache.capacity}")
    private int capacity;
    @Value("${cache.minPercent}")
    private double minPercent;
    private Queue<String> hashes;
    private final AtomicBoolean isCacheLoading = new AtomicBoolean(false);
    private final HashGenerator hashGeneratorImpl;

    @PostConstruct
    void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGeneratorImpl.getHashes(capacity));
    }

    public String getHash() {
        if (needsToFill()) {
            fillCache();
        }
        return hashes.poll();
    }

    @Async("threadPoolTaskExecutor")
    public void fillCache() {
        if (isCacheLoading.compareAndSet(false, true)) {
            hashGeneratorImpl.getHashesAsync(capacity - hashes.size())
                    .thenAccept(hashes::addAll).thenRun(() -> isCacheLoading.set(false));
        }
    }

    private boolean needsToFill() {
        return (double) hashes.size() / capacity <= minPercent;
    }
}
