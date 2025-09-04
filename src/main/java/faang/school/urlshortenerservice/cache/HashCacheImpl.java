package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashCacheImpl implements HashCache {
    @Value("${shortener.hash.cache.min-limit-percent}")
    private int minLimitPercent;
    @Value("${shortener.hash.cache.capacity}")
    private int cacheCapacity;

    @Qualifier("hashCacheExecutor")
    private final Executor executor;
    private final HashGenerator generator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    @Qualifier("hashCacheQueue")
    private final BlockingQueue<String> cache;

    @PostConstruct
    public void init() {
        List<String> hashes = generator.generateBatch();
        cache.addAll(hashes.subList(0, Math.min(cacheCapacity, hashes.size())));
    }

    @Override
    @Transactional
    public String getHash() {
        if (isBelowLimit() && !isFilling.compareAndExchange(false, true)) {
            CompletableFuture.runAsync(() -> {
                int fetchSize = cacheCapacity - cache.size();
                List<String> hashBatch = hashRepository.getHashBatch(fetchSize);
                cache.addAll(hashBatch);
                if (hashBatch.size() < fetchSize) {
                    generator.generateBatch();
                }
            }, executor).whenComplete((res, ex) -> {
                isFilling.set(false);
                if (ex != null) {
                    log.error("Error while filling cache", ex);
                }
            });
        }
        return cache.poll();
    }

    private boolean isBelowLimit() {
        return (double) cache.size() / cacheCapacity * 100 <= minLimitPercent;
    }
}
