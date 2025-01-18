package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.entity.hash.Hash;
import faang.school.urlshortenerservice.service.hash.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
public class HashCacheImpl implements HashCache {

    @Value("${hash.cache.min-percent:20}")
    private static int MIN_CACHE_PERCENT;
    @Value("${hash.cache.max-size:10000}")
    private static int MAX_CACHE_SIZE;


    private final HashGenerator hashGenerator;
    private LinkedBlockingQueue<String> hashes;
    private ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() throws ExecutionException, InterruptedException {
        hashes = new LinkedBlockingQueue<>(MAX_CACHE_SIZE);
        hashes.addAll(hashGenerator.generateBatch(1000).get()
                .stream()
                .map(Hash::getHash)
                .toList());
    }

    @Override
    @SneakyThrows
    public String getHash() {
        if (isGenerationRequired()) {
            if (lock.tryLock()) {
                try {
                    addHash().join();
                } finally {
                    lock.unlock();
                }
            }
        }
        return hashes.poll();
    }

    private boolean isGenerationRequired() {
        return (hashes.size() * 100) / MAX_CACHE_SIZE < MIN_CACHE_PERCENT;
    }

    private CompletableFuture<LinkedBlockingQueue<String>> addHash() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                hashes.addAll(hashGenerator.generateBatch(MAX_CACHE_SIZE - hashes.size()).get().stream()
                        .map(Hash::getHash)
                        .toList());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            return hashes;
        }, Executors.newSingleThreadExecutor());
    }
}
