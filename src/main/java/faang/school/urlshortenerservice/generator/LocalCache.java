package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalCache {
    @Value("${app.local_hash.capacity:9000}")
    private int capacity;
    @Value("${app.local_hash.min_value:1000}")
    private int minValue;
    @Value("${app.async.local_hash_refill.pool_size:10}")
    private int threadsNumber;
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private final Queue<String> hashes = new ConcurrentLinkedQueue<>();

    private final ExecutorService refillExecutor = Executors.newFixedThreadPool(threadsNumber);

    @PostConstruct
    public void init(){
      hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash(){
        if (hashes.size() <= minValue){
            refillAsync();
        }
        return hashes.poll();
    }

    public void refillAsync() {
        if (isRefilling.compareAndSet(false, true)) {
            CompletableFuture.runAsync(() -> {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(newHashes -> {
                            hashes.addAll(newHashes);
                            log.info("Successfully refilled LocalCache with {} hashes", newHashes.size());
                        })
                        .exceptionally(ex -> {
                            log.error("Error refilling LocalCache: ", ex);
                            return null;
                        })
                        .whenComplete((result, ex) -> {
                            isRefilling.set(false);
                        });
            }, refillExecutor);
        }
    }
}
