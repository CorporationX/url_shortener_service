package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final Executor threadPoolHashCache;

    @Value("${hash-generator.cache.size}")
    private int cacheSize;
    @Value("${hash-generator.cache.min-fill}")
    private int minFill;
    private final AtomicBoolean isCacheLocked = new AtomicBoolean(false);
    private BlockingQueue<String> cache;

    @PostConstruct
    private void cacheInit() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        fillCache();
    }

    public String getHash() {
        if (cache.size() * 100 / cacheSize < minFill) {
            log.info("HashCache starter filling cache.");
            threadPoolHashCache.execute(this::fillCache);
        }
        return cache.poll();
    }

    private void fillCache() {
        if (isCacheLocked.compareAndSet(false, true)) {
            log.info("Thread {} acquired HashCache lock.", Thread.currentThread().getName());
            try {
                List<String> hashes = hashRepository.getHashBatch(cacheSize - cache.size());
                hashRepository.save(hashes);
                hashGenerator.generateBatch();
            } finally {
                isCacheLocked.set(false);
                log.info("Thread {} released HashCache lock.", Thread.currentThread().getName());
            }
        }
    }
}
