package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashService hashService;

    @Value("${hash.cache.capacity:1000}")
    private int capacity;

    @Value("${hash.fill.percent:20}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);


    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        this.hashes = new ArrayBlockingQueue<>(capacity);
        log.info("Hashes start size = " + hashes.size());
        hashes.addAll(hashService.getHashes(capacity));
        log.info("Hashes end size = " + hashes.size());
    }

    public String getHash() {
        if (hashes.size() * 100 / capacity < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Failed to generate hashes", ex);
                            }
                            filling.set(false);
                        });
           }
        }
        
        String hash = hashes.poll();
        return hash;
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(hashService.getHashes(amount));
    }
}
