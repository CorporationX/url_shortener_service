package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Resource(name = "taskExecutor")
    private final ThreadPoolTaskExecutor executor;
    @Value("${hash.cache-capacity}")
    private int cacheCapacity;
    @Value("${hash.min-filling-percentage}")
    private double minFilling;
    private final HashRepository hashRepository;
    private final HashGenerator generator;
    private BlockingQueue<Hash> cache;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void setUp() {
        cache = new ArrayBlockingQueue<>(cacheCapacity);
        refillCache();
    }

    public String getHash() {
        if (cache.size() < minFilling * cache.remainingCapacity()) {
            if (isFilling.compareAndSet(false, true)) {
                try {
                    refillCache();
                    log.info("Cache size is lower 20 percentage. Cache is refilled");
                } finally {
                    isFilling.set(false);
                }
            }
        }
        return Objects.requireNonNull(cache.poll()).getHash();
    }

    private void refillCache() {
        generator.generateBatch();
        executor.execute(() -> cache.addAll(hashRepository.getHashBatch()));
    }
}