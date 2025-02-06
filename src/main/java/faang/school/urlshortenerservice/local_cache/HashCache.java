package faang.school.urlshortenerservice.local_cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@Getter
public class HashCache {
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);
    @Value("${hashCache.cacheSize}")
    private int cacheSize;
    @Value("${hashCache.percent}")
    private int percent;
    private Queue<String> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        cache.addAll(hashGenerator.getHashes(cacheSize));
    }

    public String getHash() {
        if (!isAtLeast20PercentLeft() && isProcessing.compareAndSet(false, true)) {
            getHashesAsync(cacheSize).thenAccept(cache::addAll)
                    .thenRun(() -> isProcessing.set(false));
        }
        return cache.poll();
    }

    @Async
    public CompletableFuture<List<String>> getHashesAsync(int batchSize) {
        return CompletableFuture.completedFuture(hashGenerator.getHashes(batchSize));
    }

    private boolean isAtLeast20PercentLeft() {
        return cache.size() <= (cacheSize * percent) / 100;
    }

}
