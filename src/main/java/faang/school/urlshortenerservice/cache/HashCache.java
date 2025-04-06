package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.generator.HashAsyncService;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash-cache.capacity:1000}")
    private int capacity;

    @Value("${hash-cache.fill.percent:20}")
    private long percentToFill;

    @Value("${hash-cache.max.range:10000}")
    private int maxRange;

    private final HashGenerator hashGenerator;
    private final HashAsyncService hashAsyncService;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    private Queue<Hash> cache;

    @PostConstruct
    public void initCache() {
        cache = new ArrayBlockingQueue<>(capacity);
        cache.addAll(hashGenerator.getHashes(capacity));
    }

    public Hash getHash() {
        if (isHashFilled() && isFilling.compareAndSet(false, true)) {
            hashAsyncService.getHashesAsync(maxRange)
                    .thenAccept(cache::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
        return cache.poll();
    }

    private boolean isHashFilled() {
        return ((cache.size() / 100.0) * capacity) * 100 < percentToFill;
    }
}
