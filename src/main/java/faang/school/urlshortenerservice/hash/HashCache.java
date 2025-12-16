package faang.school.urlshortenerservice.hash;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashGenerator hashGenerator;
    private final AsyncHashProvider asyncHashProvider;

    @Value("${hash.cache.capacity:10000}")
    private int cacheCapacity;

    @Value("${hash.cache.min_percent_filling:20}")
    private int minPercentFillingCache;

    private AtomicBoolean isCacheFillingInProgress = new AtomicBoolean(false);

    private ArrayBlockingQueue<String> hashes;

    @PostConstruct
    public void fillCache() {
        hashes = new ArrayBlockingQueue<>(cacheCapacity);
        hashes.addAll(hashGenerator.getHashes(cacheCapacity));
    }

    public String getFreeHash() {
        if (calculateCurrentPercentFillingCache() < minPercentFillingCache) {
            if (isCacheFillingInProgress.compareAndSet(false, true)) {
                log.info("Cache size became less then {}%, starts hashGeneration", minPercentFillingCache);
                asyncHashProvider.getHashes(cacheCapacity)
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> isCacheFillingInProgress.set(false));
            }
        }
        return hashes.poll();
    }

    private double calculateCurrentPercentFillingCache() {
        return 100 / (double) cacheCapacity * hashes.size();
    }
}
