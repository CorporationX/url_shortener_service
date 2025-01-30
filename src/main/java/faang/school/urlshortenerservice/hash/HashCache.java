package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.cache.capacity:100}")
    private int capacity;

    @Value("${hash.cache.fillPercent:20}")
    private int fillPercent;

    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor cacheLoaderPool;
    private final HashService hashService;

    private Queue<String> hashes;
    private AtomicBoolean filling = new AtomicBoolean(false);;


    @PostConstruct
    public void init() {
        hashes = new LinkedBlockingQueue<>(capacity);
        hashGenerator.generateBatch();
        hashes.addAll(hashService.getHashBatch(capacity));

    }

    public String getHash() {
        if (hashes.size() / (capacity / 100.0) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                fillCacheAsync().thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }


    private CompletableFuture<Void> fillCacheAsync() {
        return CompletableFuture.runAsync(() -> {
            List<String> loadedHashes = hashService.getHashBatch(capacity - hashes.size());
            hashes.addAll(loadedHashes);

            if (hashService.getHashCount() < capacity) {
                hashGenerator.generateBatch();
            }
        }, cacheLoaderPool);
    }


}
