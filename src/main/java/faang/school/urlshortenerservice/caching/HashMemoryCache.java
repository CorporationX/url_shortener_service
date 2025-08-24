package faang.school.urlshortenerservice.caching;

import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashMemoryCache {

    private final HashService hashService;
    @Value("${app.hash.memory-cache-size:2000}")
    private int defaultCacheSize;
    @Value("${app.hash.memory-cache-min-percent:20.0}")
    private double minPercentLocalHash;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<String> hashCacheQueue;

    @PostConstruct
    public void init() {
        this.hashCacheQueue = new LinkedBlockingQueue<>(defaultCacheSize);
        hashCacheQueue.addAll(hashService.getHashes(defaultCacheSize));
    }

    public String getHash() {
        if (checkCurrentPercent() && isFilling.compareAndExchange(false, true)) {
            hashService.getHashesFuture(defaultCacheSize - hashCacheQueue.size())
                    .thenAccept(hashCacheQueue::addAll)
                    .whenComplete((result, throwable) -> {
                        isFilling.set(false);
                        if (throwable != null) {
                            log.error("An error occurred while asynchronously fetching hashes", throwable);
                        }
                    });
        }
        return hashCacheQueue.poll();
    }

    private boolean checkCurrentPercent() {
        return (hashCacheQueue.size() * 100.0 / defaultCacheSize) <= minPercentLocalHash;
    }
}
