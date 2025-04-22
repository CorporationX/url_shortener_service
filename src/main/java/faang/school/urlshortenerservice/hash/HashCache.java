package faang.school.urlshortenerservice.hash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${spring.cache.capacity}")
    private int cacheCapacity;

    @Value("${spring.cache.fill-percentage}")
    private float fillPercentage;

    private Queue<String> cache;

    private final AtomicBoolean cacheIsFilled = new AtomicBoolean(false);
    private final HashGenerator hashGenerator;
    private final AsyncHashGenerator asyncHashGenerator;


    @PostConstruct
    private void init() {
        cache = new ArrayBlockingQueue<>(cacheCapacity);
        cache.addAll(hashGenerator.getHashes());
    }

    public String getHash() {
        if (isCacheBelowFillThreshold() && cacheIsFilled.compareAndSet(false, true)) {
            log.info("Cache is below threshold. Starting async refill...");

            asyncHashGenerator.generatedHashAsync()
                    .thenAccept(hashes -> {
                        hashes.forEach(cache::offer);
                        log.info("Cache refill complete. Added {} hashes.", hashes.size());
                    })
                    .thenRun(() -> cacheIsFilled.set(false));
        }

        String hash = cache.poll();
        if (hash == null) {
            throw new NoSuchElementException("There are no available hash");
        }
        return hash;
    }

    private boolean isCacheBelowFillThreshold() {
        return (float) cache.size() * 100 / cacheCapacity < fillPercentage;
    }
}
