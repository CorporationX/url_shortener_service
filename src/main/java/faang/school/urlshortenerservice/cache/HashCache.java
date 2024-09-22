package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.TimeOutException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    @Value("${spring.cache.capacity}")
    private int capacity;
    @Value("${spring.cache.threshold-in-percents}")
    private int thresholdInPercents;
    private final AtomicBoolean isGeneratingHash = new AtomicBoolean(false);

    public ArrayBlockingQueue<String> cache;
    private final HashRepository hashRepository;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(capacity, true);
        hashGenerator.generateBatch();
        cache.addAll(hashRepository.getHashBatch(capacity));
    }

    public String getHash() {
        try {
            if (isCacheSizeBelowThreshold(cache.remainingCapacity()) &&
                    isGeneratingHash.compareAndSet(false, true)) {
                generateAdditional();
            }
            return cache.poll(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Error: timeout while taking hash", e);
            throw new TimeOutException("Error: timeout while taking hash" + e);
        }
    }

    protected void generateAdditional() {
        hashGenerator.generateBatch();
        cache.addAll(hashRepository.getHashBatch(cache.remainingCapacity()));
        isGeneratingHash.set(false);
    }

    private boolean isCacheSizeBelowThreshold(int remainingCapacity) {
        return capacity - remainingCapacity < capacity * thresholdInPercents / 100;
    }
}