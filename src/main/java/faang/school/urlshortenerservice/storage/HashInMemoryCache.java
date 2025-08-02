package faang.school.urlshortenerservice.storage;

import faang.school.urlshortenerservice.service.HashGiver;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashInMemoryCache {

    private final HashGiver hashGiver;
    private final Executor hashCacheExecutor;

    @Value("${app.hash.batch-size.cache-storage}")
    private int cacheSize;
    @Value("${app.hash.percentage-filling}")
    private int percentageFilling;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    private Queue<String> hashes;

    public HashInMemoryCache(
            HashGiver hashGiver,
            @Qualifier("hashCacheExecutor") Executor hashCacheExecutor
    ) {
        this.hashGiver = hashGiver;
        this.hashCacheExecutor = hashCacheExecutor;
    }

    @PostConstruct
    public void init() {
        this.hashes = new ArrayBlockingQueue<>(cacheSize);
        hashes.addAll(hashGiver.getHashes(cacheSize));
    }

    public String getHash() {
        if (requiredFillingCash() && isFilling.compareAndSet(false, true)) {
            CompletableFuture.supplyAsync(() -> hashGiver.getHashes(cacheSize), hashCacheExecutor)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
        String hash = hashes.poll();
        log.info("Get hash: {}", hash);
        return hash;
    }

    private boolean requiredFillingCash() {
        int currentPercentageFilling = hashes.size() * 100 / cacheSize ;
        return currentPercentageFilling <= percentageFilling;
    }
}
