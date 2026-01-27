package faang.school.urlshortenerservice.generator;

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
    private final HashAsyncService hashAsyncService;

    @Value("${hash.cache.capacity:10000}")
    private int cacheCapacity;

    @Value("${hash.cache.refill-threshold-percent:20}")
    private int refillThresholdPercent;

    private static final int PERCENT_TOTAL = 100;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private final Queue<String> hashes = new ArrayBlockingQueue<>(cacheCapacity);

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(cacheCapacity));
    }

    public String getHash() {
        if ((hashes.size() * PERCENT_TOTAL / cacheCapacity) < refillThresholdPercent) {
            if (filling.compareAndSet(false, true)) {
                hashAsyncService.getHashesAsync(cacheCapacity)
                        .thenAccept(hashes::addAll)
                        .whenComplete((result, ex) -> {
                            try {
                                if (ex != null) {
                                    log.error("Failed to refill hash cache (capacity={})", cacheCapacity, ex);
                                }
                            } finally {
                                filling.set(false);
                            }
                        });
            }
        }
        return hashes.poll();
    }
}

