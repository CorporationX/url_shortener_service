package faang.school.urlshortenerservice.utils;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${hashCache.lowThreshold}")
    private final double lowThreshold;

    @Value("${hashCache.maxSize}")
    private final int hashCacheSize;

    private LinkedBlockingDeque<String> cache;

    @PostConstruct
    public void initHashCache() {
        log.info("Initializing HashCache at startup");
        cache = new LinkedBlockingDeque<>(hashCacheSize);
        fillCache();
    }

    public String getHashFromCache() {

        if (cache.isEmpty()) {
            isRunning.compareAndSet(false, true);
            return hashGenerator.getHashes(1).get(0);
        }

        int percentOfTotal = (int)(hashCacheSize - hashCacheSize * lowThreshold);

        if (cache.remainingCapacity() > percentOfTotal
                && isRunning.compareAndSet(false, true)) {
                log.info("Start refresh cache");
            refreshCache();
        }

        String hash = cache.poll();
        if (hash == null) {
            throw new RuntimeException("No hashes available in the cache");
        }
        return hash;
    }

    public void fillCache() {
        List<String> hashes = hashGenerator.getHashes(hashCacheSize);
        addHashes(hashes);
    }

    private void refreshCache() {
        CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(hashCacheSize), taskExecutor)
                .thenAccept(this::addHashes)
                .exceptionally(ex -> {
                    isRunning.set(false);
                    throw new RuntimeException(ex);
                })
                .thenRun(() -> isRunning.set(false));
    }

    private void addHashes(List<String> newHashes) {
        log.info("Push {}", newHashes.size());
        for (String hash : newHashes) {
            if (!cache.offer(hash)) {
                break;
            }
        }
    }
}
