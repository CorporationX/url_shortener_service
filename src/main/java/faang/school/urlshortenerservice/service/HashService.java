package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.properties.CacheProperties;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashGenerator hashGenerator;
    private final HashCache hashCache;
    private final JdbcHashRepository jdbcHashRepository;
    private final CacheProperties cacheProperties;

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    public String getNextHash() {
        maybeTriggerRefill();

        String hash = hashCache.poll();
        if (hash == null) {
            List<String> fallback = jdbcHashRepository.getAndRemoveBatch(cacheProperties.getFillSize());
            if (fallback.isEmpty()) {
                throw new IllegalStateException("No hashes available");
            }
            hashCache.addAll(fallback);
            hash = hashCache.poll();
        }

        return hash;
    }

    private void maybeTriggerRefill() {
        if (hashCache.size() < cacheProperties.getMinSize()
                && refillInProgress.compareAndSet(false, true)) {
            generateMoreHashes();
        }
    }

    public void generateMoreHashes() {
        CompletableFuture
                .supplyAsync(hashGenerator::generateBatch)
                .thenAccept(hashes -> {
                    hashCache.addAll(hashes);
                    refillInProgress.set(false);
                })
                .exceptionally(ex -> {
                    log.error("Failed to generate hashes", ex);
                    refillInProgress.set(false);
                    return null;
                });
    }
}