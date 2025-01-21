package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.HashService;
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
    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private Queue<String> hashQueue;
    @Value("${app.cache_capacity:1000}")
    private int cacheCapacity;
    @Value("${app.cache_to_store_coefficient:2}")
    private int coefficient;
    @Value("${app.cache_min_volume_percent:20}")
    private int minVolumePercent;
    private final AtomicBoolean isAlreadyFilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        this.hashQueue = new ArrayBlockingQueue<>(cacheCapacity);
        if (shouldGenerateNewHashes()) {
            hashGenerator.generateBatch().join();
        }
        List<String> hashes = hashService.getHashBatch(cacheCapacity).join();
        hashQueue.addAll(hashes);
    }

    public String getHash() {
        if (shouldFillCache() && !isAlreadyFilling.get()) {
            isAlreadyFilling.set(true);
            int count = cacheCapacity - hashQueue.size();
            hashService.getHashBatch(count)
                    .thenAccept(hashes -> {
                        hashQueue.addAll(hashes);
                        if (shouldGenerateNewHashes()) {
                            hashGenerator.generateBatch();
                        }
                        isAlreadyFilling.set(false);
                    });
        }
        return hashQueue.poll();
    }

    public boolean shouldGenerateNewHashes() {
        return hashService.count() < (long) cacheCapacity * coefficient;
    }

    public boolean shouldFillCache() {
        return hashQueue.size() < cacheCapacity * minVolumePercent / 100;
    }
}