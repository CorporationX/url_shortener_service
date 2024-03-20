package faang.school.urlshortenerservice.cach;

import faang.school.urlshortenerservice.config.threadpool.ThreadPoolConfig;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolConfig threadPoolConfig;
    private final Lock lock = new ReentrantLock();
    @Value("${cache.size}")
    private int cacheSize;
    @Value("${cache.fill-threshold}")
    private double fillThreshold;
    private final ArrayBlockingQueue<String> hashCash = new ArrayBlockingQueue<>(1000);

    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        List<String> hashes = hashRepository.getHashBatch(cacheSize);
        hashCash.addAll(hashes);
    }

    public String getHash() {
        String hash = null;
        try {
            if (hashCash.size() > fillThreshold * cacheSize) {
                hash = hashCash.take();
            } else {
                hash = hashCash.take();
                fillCache(cacheSize - hashCash.size());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error while getting hash", e);
        }
        return hash;
    }

    private void fillCache(int numberOfValue) {
        if (lock.tryLock()) {
            try {
                CompletableFuture.supplyAsync(() -> hashRepository.getHashBatch(numberOfValue),
                                threadPoolConfig.executorService())
                        .thenAcceptAsync(hashBatch -> {
                            hashCash.addAll(hashBatch);
                            hashGenerator.generateBatch(numberOfValue);
                        }, threadPoolConfig.executorService());
            } finally {
                lock.unlock();
            }
        }
    }
}
