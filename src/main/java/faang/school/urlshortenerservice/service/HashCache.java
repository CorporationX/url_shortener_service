package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.size}")
    private int hashCacheSize;
    private final HashGenerator hashGenerator;
    private final Queue<String> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean isRunning = false;
    private final ReentrantLock lock = new ReentrantLock();

    public String getHash() {
        String hash = queue.poll();
        if (queue.size() < hashCacheSize * 0.2) {
            fillCache();
        }
        return hash;
    }

    @Async("hashCacheExecutor")
    public void fillCache() {
        if (isRunning) {
            return;
        }
        lock.lock();

        try {
            if (!isRunning) {
                isRunning = true;
                hashGenerator.generateBatch()
                        .thenAccept(queue::addAll)
                        .whenComplete((res, ex) -> isRunning = false);
            }
        } finally {
            lock.unlock();
        }
    }
}
