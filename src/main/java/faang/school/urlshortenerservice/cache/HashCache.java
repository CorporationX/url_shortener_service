package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${cache.hash-cache.size}")
    private int cacheSize;
    @Value("${cache.hash-cache.lowest-load-barrier}")
    private int lowestLoadBarrier;

    private ArrayBlockingQueue<Hash> hashes;
    private AtomicBoolean isRefilling;

    @PostConstruct
    public void init() {
        isRefilling = new AtomicBoolean(false);
        hashes = new ArrayBlockingQueue<>(cacheSize);
        fillCacheAndDatabase(cacheSize);
    }

    public Hash getHash() {
        if (hashes.size() <= cacheSize - (cacheSize * (double) (lowestLoadBarrier / 100))
        && isRefilling.compareAndSet(false, true)) {
            log.info("Start refilling cache. Planned quantity of new hashes: {}", cacheSize);
            fillCacheAndDatabase(cacheSize)
                    .thenRun(() -> isRefilling.set(false));
        }
        return hashes.poll();
    }

    @Async
    public CompletableFuture<Void> fillCacheAndDatabase(int size) {
        return hashGenerator.getHashesAsync(size)
                .thenAccept(hashes::addAll)
                .thenRun(() -> log.info("{} of hashes was added to cache and database", cacheSize));
    }
}
