package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${hash.capacity:1000}")
    private int capacity;

    @Value("${hash.threshold.percent:0.2}")
    private double thresholdPercent;

    @Value("${hash.wanted.percent:0.8}")
    private double wantedPercent;

    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    private Queue<Hash> freeHashes;

    @PostConstruct
    public void init() {
        this.freeHashes = new ArrayBlockingQueue<>(capacity);
        freeHashes.addAll(hashGenerator.getStartHashes());
    }

    @Async("value = hashCacheExecutor")
    public Hash getHash() {
        Hash hash = freeHashes.poll();

        if (freeHashes.size() < capacity * thresholdPercent) {
            if (isGenerating.compareAndSet(false, true)) {
                fillHashesAsync();
            }
        }

        return hash;
    }

    private void fillHashesAsync() {
        hashGenerator.getHashBatch().whenComplete((hashes, ex) -> {
            try {
                if (ex != null) {
                    log.error("Error during hash generation", ex);
                } else if (hashes != null && !hashes.isEmpty()) {
                    freeHashes.addAll(hashes);
                }
                if (freeHashes.size() < capacity * wantedPercent) {
                    fillHashesAsync();
                }
            } finally {
                if (freeHashes.size() >= capacity * wantedPercent) {
                    isGenerating.set(false);
                }
            }
        });
    }

}
