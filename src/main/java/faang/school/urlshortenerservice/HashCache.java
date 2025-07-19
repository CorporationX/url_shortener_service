package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.FreeHashRepository;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final FreeHashRepository freeHashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService hashCacheExecutorService;
    private final HashCacheProperties hashCacheProperties;

    private final ConcurrentLinkedDeque<String> cache = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @PostConstruct
    void warmUp() {
        hashCacheExecutorService.submit(this::refill);
    }

    public String getHash() {
       String hash = cache.poll();

       if (hash == null) {
           log.warn("Cache is empty, getting one hash from DB synchronously");
           hash = freeHashRepository.fetchFreeHash();
       }
        triggerRefillNeeded();
       return hash;
    }

    private void triggerRefillNeeded() {
        int currentSize = cache.size();
        int capacity = hashCacheProperties.getMaxSize();

        if (currentSize <= capacity * hashCacheProperties.getRefillThreshold()
                && refillInProgress.compareAndSet(false, true)) {
            log.info("In cache only {} from {} hashes left – executing async refilling", currentSize, capacity);
            hashCacheExecutorService.submit(this::refill);
        }
    }

    private void refill() {
        try {
            int capacity = hashCacheProperties.getMaxSize();
            int toFetch = capacity - cache.size();
            if (toFetch <= 0) {
                return;
            }

            List<String> fresh = freeHashRepository.fetchFreeHashes(toFetch);
            for (String h : fresh) {
                if (cache.size() >= capacity) break;
                cache.addLast(h);
            }
            log.info("Fetched {} hashes from DB", fresh.size());

            hashCacheExecutorService.submit(hashGenerator::generateBatch);
        } catch (Exception e) {
            log.error("Error while refilling cache", e);
        } finally {
            refillInProgress.set(false);
        }
    }
}
