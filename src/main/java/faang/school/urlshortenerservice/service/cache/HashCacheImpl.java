package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.hash.Hash;
import faang.school.urlshortenerservice.service.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    @Value("${hash.cache.min-percent:20}")
    private int MIN_CACHE_PERCENT;
    @Value("${hash.cache.max-size:100}")
    private int MAX_CACHE_SIZE;


    private final HashGenerator hashGenerator;
    private LinkedBlockingQueue<String> hashes;
    private ReentrantLock lock = new ReentrantLock(true);

    @SneakyThrows
    @PostConstruct
    public void init() {
        hashes = new LinkedBlockingQueue<>(MAX_CACHE_SIZE);
        hashes.addAll(hashGenerator.getHashes(MAX_CACHE_SIZE * MIN_CACHE_PERCENT / 100));
    }

    @Override
    @SneakyThrows
    public String getHash() {
        if (isGenerationRequired()) {
            if (lock.tryLock()) {
                addHashes()
                        .thenRun(lock::unlock);
            }
        }
        return hashes.poll();
    }

    private boolean isGenerationRequired() {
        return (hashes.size() * 100) / MAX_CACHE_SIZE < MIN_CACHE_PERCENT;
    }

    private CompletableFuture<LinkedBlockingQueue<String>> addHashes() {
        return CompletableFuture.supplyAsync(() -> {
            hashes.addAll(hashGenerator.getHashes(MAX_CACHE_SIZE - hashes.size()));
            return hashes;
        }, Executors.newSingleThreadExecutor());
    }
}
