package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    private Queue<Hash> freeHashes;

    @PostConstruct
    public void init() {
        this.freeHashes = new ArrayBlockingQueue<>(capacity);
        freeHashes.addAll(hashGenerator.getStartHashes((int)(capacity*thresholdPercent)));
    }

    public Hash getHash() {
        if (isGenerating.compareAndSet(false, true)) {
            hashGenerator.getHashes().whenComplete((hashes, ex) -> {
                try {
                    if (hashes != null && !hashes.isEmpty()) {
                        for (Hash h : hashes) {
                            freeHashes.offer(h);
                        }
                    }
                    if (ex != null) {
                        log.error(ex.getMessage());
                    }
                } finally {
                    isGenerating.set(false);
                }
            });
        }
        return freeHashes.poll();
    }
}
