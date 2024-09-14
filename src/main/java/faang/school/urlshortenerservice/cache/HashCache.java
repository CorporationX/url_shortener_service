package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.size}")
    private Double CACHE_SIZE;

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
        if (hashCacheQueue.size() / CACHE_SIZE <= 0.20) {
            cache();
        }
        return hashCacheQueue.poll();
    }

    public void cache() {
        hashCacheThreadPool.execute(() -> {
            if (lock.tryLock()) {
                cacheHashes();
                lock.unlock();
            }
        });
    }

    private void cacheHashes() {
        hashGenerator.generateBatch();
        List<String> hashBatch = hashRepository.getHashBatch();
        hashCacheQueue.addAll(hashBatch);
    }
}
