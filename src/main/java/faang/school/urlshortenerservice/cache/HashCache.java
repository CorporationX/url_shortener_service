package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    private AtomicBoolean isRefilling;
    private LinkedBlockingQueue<Hash> hashQueue;

    @Value("${app.hash_cache.size}")
    private int cacheSize;
    @Value("${app.hash_cache.threshold-percentage}")
    private int thresholdPercentage;

    @PostConstruct
    public void init() {
        hashGenerator.generateBatch();
        isRefilling = new AtomicBoolean(false);

        log.info("Starting init HashCache");
        hashQueue = new LinkedBlockingQueue<>(cacheSize);
        refillCache();
    }

    public Hash getHash() {
        if (hashQueue.size() > cacheSize * thresholdPercentage / 100) {
            return hashQueue.poll();
        } else if (isRefilling.compareAndSet(false, true)) {
            refillCache();
            hashGenerator.generateBatch();
        }
        return hashQueue.poll();
    }

    @Async("asyncExecutor")
    public void refillCache() {
        int batch = cacheSize - hashQueue.size();
        hashQueue.addAll(hashRepository.getHashBatch(batch));
        isRefilling.set(false);
    }
}
