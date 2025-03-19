package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.cache.threshold}")
    private double threshold;

    private final BlockingQueue<String> cache = new LinkedBlockingQueue<>();
    private final Semaphore loadingSemaphore = new Semaphore(1);

    @PostConstruct
    public void init() {
        fillCache();
    }

    public String getHash() {
        String hash = cache.poll();
        if (cache.size() <= cacheSize * threshold) {
            triggerAsyncFill();
        }
        return hash;
    }

    @Async("cacheThreadPool")
    private void triggerAsyncFill() {
        if (loadingSemaphore.tryAcquire()) {
            try {
                fillCache();
            } finally {
                loadingSemaphore.release();
            }
        }
    }

    private void fillCache() {
        List<Hash> hashEntities = hashRepository.getHashBatch(cacheSize);
        List<String> hashes = hashEntities.stream()
                .map(Hash::getHash)
                .toList();

        if (hashes.isEmpty()) {
            hashGenerator.generateBatch();
        } else {
            cache.addAll(hashes);
        }
    }
}
