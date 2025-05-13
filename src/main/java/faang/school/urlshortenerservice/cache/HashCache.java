package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.percent}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private Queue<String> hashes;

    @Async("asyncHashGenerator")
    public CompletableFuture<List<String>> getHashBatchAsync(long amount) {
        return CompletableFuture.completedFuture(hashGenerator.getHashBatch(amount));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashBatch(capacity));
    }

    public String getHash() {
        if (hashes.size() / (capacity * 100.0) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                getHashBatchAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }
}
