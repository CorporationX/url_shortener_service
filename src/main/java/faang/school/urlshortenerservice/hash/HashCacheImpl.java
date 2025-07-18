package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import faang.school.urlshortenerservice.repository.HashRepositoryJdbcImpl;
import faang.school.urlshortenerservice.util.LockUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {
    private final ConstantsProperties constantsProperties;
    private final HashGenerator generator;
    private final HashRepositoryJdbcImpl repository;
    private final Executor taskExecutor;

    private final ReentrantLock lock = new ReentrantLock();
    private ConcurrentLinkedQueue<String> cache;
    private int cacheGenThreshold;

    @PostConstruct
    private void init() {
        cacheGenThreshold = constantsProperties.getLocalCachingSize() *
                constantsProperties.getGenerationThresholdPercent() / 100;
        cache = new ConcurrentLinkedQueue<>();
        checkAndRefillFreeHashesLeft();
    }

    @Override
    public String getHash() {
        taskExecutor.execute(this::checkAndRefillFreeHashesLeft);
        return cache.poll();
    }

    private void checkAndRefillFreeHashesLeft() {
        if (cache.size() > cacheGenThreshold) return;

        LockUtil.withLock(lock, () -> {
            generator.generateBatch();
            List<String> hashBatch = repository.getHashBatch();
            cache.addAll(hashBatch);
        });
    }
}
