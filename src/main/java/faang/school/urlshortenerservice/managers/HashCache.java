package faang.school.urlshortenerservice.managers;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repozitory.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
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
        log.info("Initializing hash cache...");
        try {
            List<Hash> initialHashes = hashGenerator.getHashes();
            initialHashes.stream()
                    .map(Hash::getHash)
                    .forEach(hashQueue::offer);
            log.info("Hash cache initialized with {} hashes", hashQueue.size());
        } catch (Exception e) {
            log.error("Failed to initialize hash cache", e);
            throw new IllegalStateException("Critical error: Unable to initialize HashCache", e);
        }
    }

    public String getHash() {
        if (hashQueue.size() <= threshold * cacheSize) {
            hashExecutor.submit(hashGenerator::generateBatch);
        }
        return hashQueue.poll();
    }

    private void triggerAsyncFetch() {
        if (isFetching.compareAndSet(false, true)) {
            CompletableFuture.supplyAsync(() -> {
                log.info("Fetching hashes from generator...");
                return hashGenerator.getHashes();
            }, hashExecutor).thenAccept(hashes -> {
                if (hashes.isEmpty()) {
                    log.info("Generating new hashes...");
                    hashGenerator.generateBatch();
                }
                hashes.stream()
                        .map(Hash::getHash)
                        .forEach(hashQueue::offer);
                log.info("Hash cache refilled, size: {}", hashQueue.size());
            }).exceptionally(ex -> {
                log.error("Error fetching hashes", ex);
                return null;
            }).whenComplete((res, ex) -> isFetching.set(false));
        }
    }
}
