package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
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
    @Value("${hashcache.capacity}")
    private int capacity;
    @Value("${hashcache.min-size}")
    private double minSize;
    private final Queue<String> hashes = new ArrayBlockingQueue<>(capacity);
    private final AtomicBoolean isCacheLoading = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        loadHashesToCache();
    }

    public String getHash() {
        if (needsToFill()) {
            loadHashesToCacheAsync();
        }
        return hashes.poll();
    }

    @Async("hashCacheThreadPool")
    public void loadHashesToCacheAsync() {
        if (isCacheLoading.compareAndSet(false, true)) {
            loadHashesToCache();
            isCacheLoading.set(false);
        }
    }

    private void loadHashesToCache() {
        hashes.addAll(
                hashGenerator.getHashes(capacity - this.hashes.size()).stream()
                        .map(Hash::getHash)
                        .toList()
        );

        hashGenerator.generateBatchAsync();
    }

    private boolean needsToFill() {
        return (double) hashes.size() / capacity <= minSize;
    }
}
