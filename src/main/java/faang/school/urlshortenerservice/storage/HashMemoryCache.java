package faang.school.urlshortenerservice.storage;

import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashMemoryCache {
    private final HashService hashService;
    @Value("${app.hash.memory-cache-size:2000}")
    private int defaultCacheSize;
    @Value("${app.hash.memory-cache-min-percent:20.0}")
    private double minPercentLocalHash;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    // TODO: передать capacity в конструктор
    private final Queue<String> hashCacheQueue = new LinkedBlockingQueue<>();

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        hashCacheQueue.addAll(hashService.getHashes(defaultCacheSize));
    }

    public String getHash() {
        if (isFilling.compareAndExchange(false, true) && checkCurrentPercent()) {
            hashService.getHashesAsync(defaultCacheSize - hashCacheQueue.size())
                    .thenAccept(hashCacheQueue::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
        return hashCacheQueue.poll();
    }

    private boolean checkCurrentPercent() {
        return (hashCacheQueue.size() * 100.0 / defaultCacheSize) <= minPercentLocalHash;
    }
}
