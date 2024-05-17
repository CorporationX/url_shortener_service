package faang.school.urlshortenerservice.Cache;

import faang.school.urlshortenerservice.service.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.cache-size}")
    private int capacity;

    @Value("${hash.cache-min-fill-percent}")
    private int minFillPercent;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    private final HashGenerator hashGenerator;

    private Queue<String> hashes;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(hashGenerator.getHashes(capacity));
    }

    public String getHash() {
        if (isCacheRunningOut()) {
            if (isFilling.compareAndSet(false, true)) {
                hashGenerator.getHashesAsync(capacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isFilling.set(false));
            }
        }
        return hashes.poll();
    }

    public void addHashes(List<String> newHashes) {
        hashes.addAll(newHashes);
    }

    private boolean isCacheRunningOut() {
        return ((hashes.size() / (double) capacity) * 100.0) < minFillPercent;
    }
}