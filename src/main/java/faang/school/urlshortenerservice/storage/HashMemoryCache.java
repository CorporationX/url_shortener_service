package faang.school.urlshortenerservice.storage;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashMemoryCache {
    private final HashService hashService;
    @Value("${app.hash.memory-cache-size:2000}")
    private int hashSize;
    @Value("${app.hash.memory-cache-min-percent:20.0}")
    private double minPercentLocalHash;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final Queue<String> hashCacheQueue = new LinkedBlockingQueue<>(hashSize);

    @PostConstruct
    public void init() {
        hashCacheQueue.addAll(hashService.getHashes(hashSize));
    }

    public String getHash() {
        if (isFilling.compareAndExchange(false, true) && checkCurrentPercent()) {
            hashService.getHashesAsync(hashSize - hashCacheQueue.size())
                    .thenAccept(hashCacheQueue::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
        return hashCacheQueue.poll();
    }

    private boolean checkCurrentPercent() {
        return (hashCacheQueue.size() * 100.0 / hashSize) <= minPercentLocalHash;
    }
}
