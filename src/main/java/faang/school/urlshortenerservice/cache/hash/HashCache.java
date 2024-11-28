package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.config.properties.hash.HashProperties;
import faang.school.urlshortenerservice.generator.hash.HashGenerator;
import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Component
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final HashRepository hashRepository;

    private final Queue<String> cache;
    private final int capacity;
    private final double fillPercent;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @Autowired
    public HashCache(HashGenerator hashGenerator,
                     HashProperties hashProperties,
                     HashRepository hashRepository) {
        this.hashGenerator = hashGenerator;
        this.hashProperties = hashProperties;
        this.hashRepository = hashRepository;
        this.capacity = hashProperties.getCache().getCapacity();
        this.fillPercent = hashProperties.getCache().getFillPercent();
        this.cache = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    public void initCache() {
        log.debug("Staring to init cache with size : {}", capacity);
        hashGenerator.generateBatchForCache(capacity).thenAccept(this::fillUpCache);
    }

    public String getOneHash() {
        boolean isCacheLow = (double) cache.size() / (double) capacity < fillPercent;
        log.debug("Getting one hash from cache");
        if (isCacheLow && isFilling.compareAndSet(false, true)) {
            log.debug("Cache capacity is lower than {} % , starting to generate additional caches", fillPercent);
            hashGenerator.generateBatch();
            CompletableFuture.runAsync(() -> {
                fillUpCache(hashRepository.getAndDeleteHashBatch(capacity - cache.size()));
                log.info("Generated hashes for cache successfully now cache size is {}", cache.size());
                isFilling.set(false);
            });
        }
        return cache.poll();
    }

    private void fillUpCache(List<Hash> hashes) {
        cache.addAll(hashes.stream()
                .map(Hash::getHash)
                .toList());
    }
}
