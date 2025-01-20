package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.queue.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.threshold-percent:20}")
    private double thresholdPercent;

    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);
    private Queue<String> hashes;

    private int thresholdCache;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        thresholdCache = (int)(capacity * thresholdPercent / 100);

        List<String> generatedHashes = hashGenerator.getHashList(capacity);
        try {
            hashes.addAll(generatedHashes);
        } catch (IllegalStateException e) {
            log.error("Error initializing HashCache: Queue is full", e);
        }
    }

    public String getHash() {
        if (checkRefresh()) {
            refreshCache();
        }
        return hashes.poll();
    }

    private void refreshCache() {
        if (isRefreshing.compareAndSet(false, true)) {
            hashGenerator.getHashListAsync(capacity)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isRefreshing.set(false));
        }
    }

    private boolean checkRefresh() {
        return hashes.size() < thresholdCache;
    }
}
