package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final Lock lock = new ReentrantLock();
    @Value("${cache.size}")
    private int cacheSize;
    @Value("${cache.fill-threshold}")
    private double fillThreshold;
    private ArrayBlockingQueue<String> hashCacheQueue;

    public HashCache(HashRepository hashRepository, HashGenerator hashGenerator, @Qualifier("threadPool") ThreadPoolTaskExecutor taskExecutor) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.taskExecutor = taskExecutor;
    }

    @PostConstruct
    public void init() {
        int countOfHashes = hashRepository.count();
        hashCacheQueue = new ArrayBlockingQueue<>(cacheSize);
        List<String> hashes = hashRepository.getHashBatch(cacheSize);
        if (hashes.isEmpty()) {
            hashGenerator.generateBatch(cacheSize - countOfHashes);
            hashes = hashRepository.getHashBatch(cacheSize);
        }
        hashCacheQueue.addAll(hashes);
        log.info("Hash cache initialized with number of elements {}", hashes.size());
    }

    public String getHash() {
        String hash = "";
        try {
            if (hashCacheQueue.size() > fillThreshold * cacheSize) {
                hash = hashCacheQueue.take();
            } else {
                hash = hashCacheQueue.take();
                fillCache(cacheSize - hashCacheQueue.size());
            }
        } catch (InterruptedException e) {
            log.error("Error while getting hash", e);
            Thread.currentThread().interrupt();
        }
        return hash;
    }

    private void fillCache(int count) {
        if (lock.tryLock()) {
            CompletableFuture.runAsync(() -> {
                        hashCacheQueue.addAll(hashRepository.getHashBatch(count));
                        log.info("Hash cache filled");
                        hashGenerator.generateBatch(count);
                    }, taskExecutor)
                    .thenRun(lock::unlock);
        }
    }
}
