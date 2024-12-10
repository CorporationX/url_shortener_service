package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepositoryImpl hashRepositoryImpl;

    @Value("${hash.cache.capacity-hash:10000}")
    private int capacity;
    @Value("${hash.cache.fill.percent:0.2}")
    private double percent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache with capacity: {}, fillPercent: {}", capacity, percent);
        hashes = new ArrayBlockingQueue<>(capacity);
        CompletableFuture<List<String>> future =
                hashGenerator.getHashes(capacity);
        future.thenAccept(result -> {
            hashes.addAll(result);
            log.info("Hashes size / DB size: {}/{}", hashes.size(), hashRepositoryImpl.count());
        });
    }

    public String getHash() {
        if (hashes.size() < capacity * percent) {
            log.info("Filling cache...");
            if (filling.compareAndSet(false, true)) {
                hashGenerator.getHashes(capacity - hashes.size())
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false))
                        .exceptionally(ex -> {
                            log.error("Error filling cache: {}", ex.getMessage(), ex);
                            filling.set(false);
                            throw new CompletionException(ex);
                        });
            }
        }
        return hashes.poll();
    }
}
