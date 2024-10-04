package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.size}")
    private Integer cacheSize;
    @Value("${hash.cache.min-percentage}")
    private Integer cacheMinPercentage;

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ThreadPoolExecutor hashCacheThreadPool;
    private final ArrayBlockingQueue<String> hashCacheQueue;
    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        cacheHashes();
    }

    public String getHash() {
        if ((double) hashCacheQueue.size() / cacheSize <= cacheMinPercentage) {
            cache();
        }
        return hashCacheQueue.poll();
    }

    public void cache() {
        hashCacheThreadPool.execute(() -> {
            if (lock.tryLock()) {
                try {
                    cacheHashes();
                } finally {
                    lock.unlock();
                }
            }
        });
    }

    private void cacheHashes() {
        hashGenerator.generateBatch();
        hashRepository.getHashBatch().forEach(hashCacheQueue::offer);
    }
}
