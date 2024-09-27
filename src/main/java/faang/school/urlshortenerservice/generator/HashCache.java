package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final TaskExecutor hashCacheExecutor;

    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.fill_percent}")
    private int fillPercent;
    private BlockingQueue<String> hashes;
    private AtomicBoolean isCacheLoading = new AtomicBoolean(false);

    public String getHash() {
        if (hashes.size() < (capacity * fillPercent) / 100 &&
                (isCacheLoading.compareAndSet(false, true))) {
            hashCacheExecutor.execute(() -> {
                fillCache();
            });
        }
        try {
            return hashes.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting hash", e);
        }
    }

    private void fillCache() {
        try {
            hashes.addAll(hashRepository.getHashBatch(capacity - hashes.size()));

            if (hashes.size() < capacity) {
                CompletableFuture<Void> future = hashGenerator.generateBatchAsync();
                future.get();
                hashes.addAll(hashRepository.getHashBatch(capacity - hashes.size()));
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log.error("Exception while filling hashCache", e);
        } finally {
            isCacheLoading.set(false);
        }
    }

    @PostConstruct
    private void firstFill() {
        hashes = new LinkedBlockingQueue<>(capacity);
        fillCache();
    }
}
