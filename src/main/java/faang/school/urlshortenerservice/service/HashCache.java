package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepositoryJdbc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private static final Queue<String> hashes = new ConcurrentLinkedQueue<>();

    private final ReentrantLock lock = new ReentrantLock();
    private final HashRepositoryJdbc hashRepositoryJdbc;
    private final HashGenerator hashGenerator;

    @Value("${url.hash.cache-size-percent}")
    private int cacheSizePercent;
    @Value("${url.hash.cache-size}")
    private int cacheSize;


    public String getHash() {
        if (hashes.size() / cacheSize * 100 <= cacheSizePercent) {
            if (!lock.isLocked()) {
                loadHashQueue();
            }
        }
        return hashes.poll();
    }

    public void loadHashQueue() {
        CompletableFuture.runAsync(() -> {
            lock.lock();
            try {
                hashes.addAll(hashRepositoryJdbc.getHashBatch());
                hashGenerator.generateBatch();
            } finally {
                lock.unlock();
            }
        });
    }
}
