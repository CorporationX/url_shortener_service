package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;

    @Value("${hash.cache.capacity.value}")
    private int capacityValue;

    @Value("${hash.cache.capacity.min_percent}")
    private double minCapacityPercent;

    private final HashGenerator hashGenerator;
    private AtomicBoolean isFilling;
    private ArrayBlockingQueue<Hash> hashQueue;
    private final ThreadPoolTaskExecutor hashExecutor;

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache in thread {}", Thread.currentThread());
        hashQueue = new ArrayBlockingQueue<>(capacityValue);
        isFilling = new AtomicBoolean(false);
        hashExecutor.execute(this::populateCacheAsync);
        hashGenerator.generateBatchAsync(capacityValue);
    }
    @Transactional
    public Hash getHash() {
        checkCache();
        return hashQueue.poll();
    }

    public void checkCache() {
        if ((double) hashQueue.size() / capacityValue * 100 < minCapacityPercent) {
            if (isFilling.compareAndSet(false, true)) {
                hashExecutor.execute(() -> populateCacheAsync());
                log.info("Returned to thread {}", Thread.currentThread());
            }
        }
    }

    @Async("hashExecutor")
    public void populateCacheAsync() {
        log.info("Populating cache in thread {}", Thread.currentThread());
        int batch = capacityValue - hashQueue.size();
        hashQueue.addAll(hashRepository.getHashBatch(batch));
        hashGenerator.generateBatch(batch);
        isFilling.set(false);
    }
}
