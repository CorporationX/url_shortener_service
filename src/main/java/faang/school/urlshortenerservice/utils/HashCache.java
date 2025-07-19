package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashService hashService;
    @Value("${app.hash.table-size:10000}")
    private int hashSize;
    @Value("${app.hash.memory-cache-min-percent:20.0}")
    private double minPercentLocalHash;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    // TODO: посмотреть другие виды очередей
    private final Queue<String> hashCacheQueue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        hashCacheQueue.addAll(hashService.getHashes(hashSize));
    }

    public String getHash() {
        if (isFilling.compareAndExchange(false, true) && checkCurrentPercent()) {
            hashService.getHashesAsync(15)
                    .thenAccept(hashCacheQueue::addAll)
                    .thenRun(() -> isFilling.set(false));
        }
        return hashCacheQueue.poll();
    }

    private boolean checkCurrentPercent() {
        return (hashCacheQueue.size() * 100.0 / hashSize) <= minPercentLocalHash;
    }
}
