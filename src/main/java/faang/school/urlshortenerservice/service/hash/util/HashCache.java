package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
public class HashCache {

    private final HashService hashService;
    private final HashGenerator hashGenerator;


    @Value("${app.hash_cache.hashes_max_size:1000}")
    private int hashesMaxSize;


    @Value("${app.hash_cache.hashes_min_size:200}")
    private int hashesMinSize;

    private final Queue<String> cacheQueue = new ConcurrentLinkedDeque<>();


    private final AtomicBoolean isUpdating = new AtomicBoolean(false);


    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public HashCache(HashService hashService, HashGenerator hashGenerator) {
        this.hashService = hashService;
        this.hashGenerator = hashGenerator;

        isUpdating.set(true);
        try {
            refillCache();
        } finally {
            isUpdating.set(false);
        }
    }


    public String getHash() {
        checkAndRefillIfNeeded();

        return cacheQueue.poll();
    }

    private void checkAndRefillIfNeeded() {
        if (cacheQueue.size() < hashesMinSize) {
            if (isUpdating.compareAndSet(false, true)) {
                executorService.submit(() -> {
                    try {
                        refillCache();
                        hashGenerator.generate();
                    } catch (Exception e) {
                        log.error("Error while refilling HashCache", e);
                    } finally {
                        isUpdating.set(false);
                    }
                });
            }
        }
    }

    private void refillCache() {
        int needCount = hashesMaxSize - cacheQueue.size();
        if (needCount > 0) {
            log.info("Refilling HashCache with {} hashes...", needCount);

            List<String> newHashes = hashService.findAllByPackSize(needCount);

            cacheQueue.addAll(newHashes);

            log.info("HashCache refilled. Current size = {}", cacheQueue.size());
        }
    }
}