package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.utils.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${server.hash.cache.initial.size}")
    private int initialCacheSize;

    private LinkedBlockingQueue<String> hashes;
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void initializeCache() {
        int adjustedCount = (int) (initialCacheSize / 0.8);
        hashes = new LinkedBlockingQueue<>(adjustedCount);

        log.info("Initializing hash cache with capacity: {}", adjustedCount);

        hashGenerator.generateBatch();
        hashes.addAll(getHashBatch());

        log.info("Hash cache initialized with {} hashes.", hashes.size());
    }

    public String getHash() {
        if (shouldRefillHashes()) {
            refillHashes();
        }

        String hash = hashes.poll();
        log.debug("Retrieved hash: {}. Remaining capacity: {}", hash, hashes.size());

        return hash;
    }

    private boolean shouldRefillHashes() {
        double remainingCapacity = hashes.remainingCapacity();
        double currentSize = hashes.size();

        return remainingCapacity * 0.25 >= currentSize;
    }

    private List<String> getHashBatch() {
        return hashRepository.getHashBatch()
                .stream()
                .map(Hash::getHash)
                .toList();
    }

    private void refillHashes() {
        synchronized (this) {
            if (!shouldRefillHashes()) {
                return;
            }

            log.info("Refilling hash cache. Current size: {}, remaining capacity: {}", hashes.size(), hashes.remainingCapacity());

            executorService.execute(() -> {
                List<String> newHashes = getHashBatch();
                hashes.addAll(newHashes);
                log.info("Added {} hashes to cache. New size: {}", newHashes.size(), hashes.size());
            });

            executorService.execute(() -> {
                log.info("Triggering batch hash generation.");
                hashGenerator.generateBatch();
            });
        }
    }
}

