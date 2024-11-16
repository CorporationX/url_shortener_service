package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.config.properties.hash.HashProperties;
import faang.school.urlshortenerservice.generator.hash.HashGenerator;
import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final HashRepository hashRepository;
    private Queue<String> cache;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void initCache() {
        int capacity = hashProperties.getCache().getCapacity();
        log.debug("Staring to init cache with size : {}", capacity);
        cache = new ArrayBlockingQueue<>(capacity);
        hashGenerator.generateBatchForCache(capacity).thenAccept(this::fillUpCache);
    }

    public String getOneHash() {
        int capacity = hashProperties.getCache().getCapacity();
        double fillPercent = hashProperties.getCache().getFillPercent();
        boolean isLower = (double) cache.size() / (double) capacity < fillPercent;
        log.debug("Getting one hash from cache");
        if (isLower) {
            log.debug("Cache capacity is lower than {} % , starting to generate additional caches", fillPercent);
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.generateBatch();
                fillUpCache(hashRepository.getAndDeleteHashBatch(capacity - cache.size()));
                log.info("Generated hashes for cache successfully now cache size is {}", cache.size());
                isFilling.set(false);
            }
        }
        return cache.poll();
    }

    private void fillUpCache(List<Hash> hashes) {
        cache.addAll(hashes.stream()
                .map(Hash::getHash)
                .toList());
    }
}
