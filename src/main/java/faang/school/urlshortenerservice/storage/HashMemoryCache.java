package faang.school.urlshortenerservice.storage;

import faang.school.urlshortenerservice.service.HashService;
import faang.school.urlshortenerservice.service.HashServiceAsync;
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
    private final HashServiceAsync hashServiceAsync;
    private final AtomicBoolean isCacheRefilling = new AtomicBoolean(false);
    private Queue<String> hashCacheQueue;

    @Value("${app.hash.memory-cache-size}")
    private int defaultCacheSize;

    @Value("${app.hash.memory-cache-min-percentage}")
    private double hashMinimumPercentage;

    @PostConstruct
    public void init() {
        this.hashCacheQueue = new LinkedBlockingQueue<>(defaultCacheSize);
        hashCacheQueue.addAll(hashService.getHashes(defaultCacheSize));
    }

    public String getHash() {
        if (checkCacheFillRate() && isCacheRefilling.compareAndExchange(false, true)) {
            hashServiceAsync.getHashesAsync(defaultCacheSize - hashCacheQueue.size())
                    .thenAccept(hashCacheQueue::addAll)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Error occurred during hash fetching", exception);
                        }
                        isCacheRefilling.set(false);
                    });
        }
        return hashCacheQueue.poll();
    }

    private boolean checkCacheFillRate() {
        return (hashCacheQueue.size() * 100.0 / defaultCacheSize) <= hashMinimumPercentage;
    }
}
