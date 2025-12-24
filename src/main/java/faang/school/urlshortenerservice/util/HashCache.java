package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hash.hash-cache.get-batch}")
    private int batch;
    @Value("${hash.hash-cache.limit}")
    private int limit;
    @Value("${hash.hash-cache.bound}")
    private int bound;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService hashCacheExecutor;
    private final LinkedBlockingQueue<String> cache = new LinkedBlockingQueue<>(limit);
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache");
        if (cache.isEmpty() && isGenerating.compareAndSet(false, true)) {
            try {
                hashGenerator.generateBatch();
                cache.addAll(hashRepository.getHashBatch(batch));
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                isGenerating.set(false);
            }
        }
        log.info("Initialization of HashCache is finished");
    }

    public String getHash() {
        try {
            if (cache.size() < bound && isGenerating.compareAndSet(false, true)) {
                log.info("The size of the cache is less than 20%");
                CompletableFuture.runAsync(() -> {
                            cache.addAll(hashRepository.getHashBatch(batch));
                            hashGenerator.generateBatch();
                            log.info("The cache is filled");
                        }, hashCacheExecutor)
                        .whenComplete((result, ex) -> isGenerating.set(false))
                        .exceptionally(ex -> {
                            throw new RuntimeException(ex);
                        });
            }
            return cache.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
