package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class LocalCache {

    private final HashGenerator hashGenerator;
    private final LocalCacheRetry localCacheRetry;

    @Value("${hash.hash.cache.fill.percent:20}")
    private int fillPercent;
    @Value("${hash.hash.capacity:10000}")
    private int capacity;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> hashes;
    private int minQueueSize;

    private final String cacheEmptyExceptionMessage = "There are a lot of requests. Please, repeat in a couple of minutes.";

    @PostConstruct
    public void init() {
        log.info(" Start local cash.");
        minQueueSize = capacity * fillPercent / 100;
        hashes = new ArrayBlockingQueue<>(capacity);

        try {
            hashes.addAll(hashGenerator.getHashes(capacity));
        } catch (IllegalStateException e) {
            log.error("Error initializing HashCache: capacity: {} Queue is full:", capacity, e);
        }
    }

    public String getHash() {
        log.info(" Get hash. Local hash has: {}", hashes.size());
        if (isRequiredExtraHashes()) {
            getExtraHashes();
        }
        return getHashItem();
    }

    private boolean isRequiredExtraHashes() {
        return hashes.size() < minQueueSize;
    }

    private void getExtraHashes() {
        log.info(" Get extra hash. Local hash has: {}, filling  {}", hashes.size(), filling.get());
        if (filling.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(capacity - hashes.size())
                    .thenAccept(strings -> {
                        try {
                            hashes.addAll(strings);
                        } catch (Exception e) {
                            log.error("Error during extra update", e);
                        }
                    })
                    .thenRun(() -> {
                        filling.set(false);
                    });
        }
    }

    private String getHashItem() {
        String outHash;
        outHash = hashes.poll();
        if (outHash != null) {
            return outHash;
        } else {
            return localCacheRetry.getCachedHash(hashes);
        }
    }
}