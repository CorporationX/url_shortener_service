package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.capacity:1000}")
    private int capacity;

    @Value("${hash.cache.fill-percent:20}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private BlockingQueue<String> hashes;

    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (getCurPercent() < fillPercent) {
            log.info("Count of hashes in cache is less than {}", fillPercent);
            if (filling.compareAndSet(false, true)) {
                log.info("Generating hashes");
                getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        try {
            return hashes.take();
        } catch (InterruptedException e) {
            log.error("Error getting hash from cache: {}", e.getMessage());
            throw new RuntimeException("Error getting hash from cache: " + e.getMessage());
        }
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(hashGenerator.getHashes(amount));
    }

    private int getCurPercent() {
        return hashes.size() / capacity * 100;
    }
}
