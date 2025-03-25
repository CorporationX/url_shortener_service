package faang.school.url_shortener_service.cache;


import faang.school.url_shortener_service.generator.AsynchronousHashGenerator;
import faang.school.url_shortener_service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hash.cache.capacity}")
    private int capacity;
    @Value("${hash.cache.filled-percentage}")
    private int filledPercentage;
    @Value("${hash.batch}")
    private int hashBatchSize;

    private Queue<String> hashes;
    private final HashGenerator hashGenerator;
    private final AsynchronousHashGenerator asynchronousHashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        log.info("Initializing HashCache with capacity: {}", capacity);
        int retries = (int) Math.ceil((double) capacity / hashBatchSize);
        List<String> initialHashes = new ArrayList<>();
        for (int i = 0; i < retries && initialHashes.size() < capacity; i++) {
            List<String> batchHashes = hashGenerator.getHashes(capacity - initialHashes.size());
            initialHashes.addAll(batchHashes);
        }
        hashes.addAll(initialHashes);
        log.info("Hash Cache initialized with {} hashes (max capacity: {})", hashes.size(), capacity);
    }

    public String getHash() {
        if ((hashes.size() * 100 / capacity) < filledPercentage && isFilling.compareAndSet(false, true)) {
            log.info("Async hash refill was triggered (cache size = {})", hashes.size());
            int missing = capacity - hashes.size();
            asynchronousHashGenerator.getHashesAsynchronously(missing)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> {
                        log.info("Async refill complete. HashCache now has {} hashes", hashes.size());
                        isFilling.set(false);
                    });
        }
        return hashes.poll();
    }

    public void offerToCacheOrStoreRest(List<String> newHashes){
        List<String> remaining = new ArrayList<>();
        for(String hash : newHashes){
            if(!hashes.offer(hash)){
                remaining.add(hash);
            }
        }
        if(!remaining.isEmpty()) {
            log.info("Cache full. Storing {} leftover hashes to DB", remaining.size());
            hashGenerator.saveHashesToDb(remaining);
        } else {
            log.info("All {} hashes added to cache", newHashes.size());
        }
    }
}