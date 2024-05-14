package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${cache.capacity.value}")
    private int cacheCapacity;

    @Value("${cache.capacity.min_percent}")
    private double minCapacityPercent;

    private final HashRepository hashRepository;

    private final HashGenerator hashGenerator;

    private AtomicBoolean isFilling;

    private ArrayBlockingQueue<Hash> hashQueue;


    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue(cacheCapacity);
        isFilling = new AtomicBoolean(false);

        populateCacheAsync();
    }


    @Transactional
    public Hash getHash() {
        manageCache();
        return hashQueue.remove();

    }


    public void manageCache() {
        if (hashQueue.size() < (double) cacheCapacity * minCapacityPercent) {
            if (isFilling.compareAndSet(false, true)) {
                populateCacheAsync();
                hashGenerator.generateBatchAsync();
            }
        }
    }

    @Async("executor")
    public void populateCacheAsync() {
        int batch = cacheCapacity - hashQueue.size();
        hashQueue.addAll(hashRepository.getHashBatch(batch));
        isFilling.set(false);
    }
}