package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.exception.NoFreeHashesException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashRepository hashRepository;
    private final Queue<String> cache = new ConcurrentLinkedQueue<>();
    private final Executor executor;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @Value("${hash.cache.min.size:20}")
    private int minCacheSize;

    @PostConstruct
    public void init() {
        log.info("Initializing hash cache on startup");
        refillCacheIfNeeded();
    }

    public String getHash() {
        String hash = cache.poll();
        if (hash == null) {
            refillCacheIfNeeded();
            hash = cache.poll();
        }

        if (hash == null) {
            throw new NoFreeHashesException("No free hashes available");
        }

        refillCacheIfNeeded();
        return hash;
    }

    private void refillCacheIfNeeded() {
        if (cache.size() >= minCacheSize) {
            return;
        }

        if (!isRefilling.compareAndSet(false, true)) {
            log.debug("Refill already in progress, skipping");
            return;
        }

        executor.execute(() -> {
            try {
                int need = minCacheSize - cache.size();

                log.info("Refilling cache, need {} hashes", need);

                List<String> hashes = hashRepository.getHashBatch(need);

                cache.addAll(hashes);

                log.info("Added {} hashes to cache from DB", hashes.size());

                if (hashes.size() < need) {
                    log.info("Not enough hashes in DB, triggering generation");
                    hashGenerator.generateBatch();
                }

            } catch (Exception e) {
                log.error("Failed to refill hash cache", e);
            } finally {
                isRefilling.set(false);
            }
        });
    }
}