package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private static final int CAPACITY = 10000;

    @Value("${hash.cache.min-fill}")
    private double minimalCacheFill;

    private final AtomicBoolean fillingCache = new AtomicBoolean(false);

    private final Queue<String> hashesCache = new LinkedBlockingQueue<>(CAPACITY);

    private final HashRepository hashRepository;

    private final HashGenerator hashGenerator;

    public HashCache(HashRepository hashRepository, HashGenerator hashGenerator) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
    }

    @PostConstruct
    public void initCache() {
        hashGenerator.generateBatch(CAPACITY);
        hashesCache.addAll(hashRepository.getHashBatch(CAPACITY));
    }

    public String getHash() {
        if ((double) hashesCache.size() / CAPACITY < minimalCacheFill) {
            if (fillingCache.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                    int deficit = CAPACITY - hashesCache.size();
                    hashGenerator.generateBatch(deficit);
                    hashesCache.addAll(hashRepository.getHashBatch(deficit));
                    fillingCache.set(false);
                });
            }
        }
        return hashesCache.poll();
    }
}
