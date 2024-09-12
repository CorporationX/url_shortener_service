package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Resource(name = "taskExecutor")
    private final ThreadPoolTaskExecutor executor;
    private final HashRepository hashRepository;
    private final HashProperties properties;
    private final HashGenerator generator;
    private BlockingQueue<Hash> cache;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void setUp() {
        cache = new ArrayBlockingQueue<>(properties.getCacheCapacity());
        refillCache();
    }

    public Hash getHash() {
        if (cache.size() < properties.getMinFillingPercentage() * cache.remainingCapacity()) {
            if (isFilling.compareAndSet(false, true)) {
                try {
                    refillCache();
                    log.info("cache size is lower 20 percentage. cache is refilled");
                } finally {
                    isFilling.set(false);
                }
            }
        }
        return cache.poll();
    }

    private void refillCache() {
        generator.generateBatch();
        executor.execute(() -> cache.addAll(hashRepository.getHashBatch()));
    }
}