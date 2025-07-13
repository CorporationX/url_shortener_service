package faang.school.urlshortenerservice.managers;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final ExecutorService hashExecutor;
    private Queue<String> hashQueue;
    private final AtomicBoolean isFetching = new AtomicBoolean(false);


    @Value("${spring.url.hash.cache.size}")
    private int cacheSize;

    @Value("${spring.url.hash.cache.threshold}")
    private double threshold;

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache with cache size: {}", cacheSize);
        hashQueue = new ArrayBlockingQueue<>(cacheSize);
        hashGenerator.getHashesSync().stream().map(Hash::getHash).forEach(hashQueue::add);
        hashQueue.forEach(hash -> log.info("Hash in cache: {}", hash));
        log.info("HashCache initialized with {} hashes", hashQueue.size());
    }

    public String getHash() {
        if (hashQueue.size() <= threshold * cacheSize) {
            hashExecutor.submit(hashGenerator::generateBatch);
        }
        return hashQueue.poll();
    }

    private void triggerAsyncFetch() {
        if (isFetching.compareAndSet(false, true)) {
            hashGenerator.getHashes()
                    .thenAcceptAsync(hashes -> {
                        if (hashes.isEmpty()) {
                            log.info("Generating new hashes...");
                            hashGenerator.generateBatch();
                        }
                        hashes.stream()
                                .map(Hash::getHash)
                                .forEach(hashQueue::offer);
                        log.info("Hash cache refilled, size: {}", hashQueue.size());
                    }, hashExecutor)
                    .exceptionally(ex -> {
                        log.error("Error fetching hashes", ex);
                        return null;
                    })
                    .whenComplete((res, ex) -> isFetching.set(false));
        }
    }

}
