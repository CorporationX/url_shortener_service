package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

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
    private final ThreadPoolTaskExecutor taskExecutor;
    private final Lock lock = new ReentrantLock();
    @Value("${cache.size}")
    private int cacheSize;
    @Value("${cache.fill-threshold}")
    private double fillThreshold;
    private ArrayBlockingQueue<String> hashCache;

    @PostConstruct
    public void init() {
        int countOfHashes = hashRepository.count();
        hashCache = new ArrayBlockingQueue<>(cacheSize);
        List<String> hashes = hashRepository.getHashBatch(cacheSize);
        if (hashes.isEmpty()) {
            hashGenerator.generateBatch(cacheSize - countOfHashes);
            hashes = hashRepository.getHashBatch(cacheSize);
        }
        hashCache.addAll(hashes);
        log.info("Hash cache initialized with number of elements {}", hashes.size());
    }

    public String getHash() {
        String hash;
        try {
            if (hashCache.size() > fillThreshold * cacheSize) {
                hash = hashCache.take();
            } else {
                hash = hashCache.take();
                fillCache(cacheSize - hashCache.size());
            }
        } catch (InterruptedException e) {
            log.error("Error while getting hash", e);
            throw new RuntimeException("Error while getting hash", e);
        }
        return hash;
    }

    private void fillCache(int count) {
        if (lock.tryLock()) {
            CompletableFuture.runAsync(() -> {
                        hashCache.addAll(hashRepository.getHashBatch(count));
                        log.info("Hash cache filled");
                        hashGenerator.generateBatch(count);
                    }, taskExecutor)
                    .thenRun(lock::unlock);
        }
    }
}
