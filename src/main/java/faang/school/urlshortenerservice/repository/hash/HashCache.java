package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    @Value("${app.hash.cache.refill.thread-amount}")
    private int threadAmount;

    @Value("${app.hash.cache.refill.threshold}")
    private int refillThreshold;

    @Value("${app.hash.batch.size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private ExecutorService executorService;
    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(threadAmount);
        refillCache();
    }

    public synchronized String getHash() {
        if (hashQueue.size() < refillThreshold && isRefilling.compareAndSet(false, true)) {
            refillCache();
        }
        return hashQueue.poll();
    }

    private void refillCache() {
        executorService.submit(() -> {
            try {
                List<String> existingHashes = hashRepository.getHashBatch(batchSize);
                hashQueue.addAll(existingHashes);

                CompletableFuture<List<String>> future = hashGenerator.generateBatch();
                future.thenAccept(newHashes -> {
                    hashQueue.addAll(newHashes);
                    log.info("Added {} new hashes to queue", newHashes.size());
                });
            } catch (Exception e) {
                log.error("Error refilling hash cache", e);
            } finally {
                isRefilling.set(false);
            }
        });
    }
}
