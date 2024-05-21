package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCache {

    @Value("${cache.capacity.value}")
    private int cacheCapacity;
    @Value("${cache.capacity.min_percent}")
    private double minCapacityPercent;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor taskExecutor;

    public HashCache(HashRepository hashRepository,
                     HashGenerator hashGenerator,
                     @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.taskExecutor = taskExecutor;
    }

    private AtomicBoolean isFilling;

    private ArrayBlockingQueue<Hash> hashQueue;

    @PostConstruct
    public void init() {
        log.info("HashCache is initialised {}", Thread.currentThread());
        hashQueue = new ArrayBlockingQueue<>(cacheCapacity);
        isFilling = new AtomicBoolean(false);
        taskExecutor.execute(this::populateCache);

        log.info("sending request to generate the hash async {}", Thread.currentThread());
        hashGenerator.generateBatchAsync();
    }


    @Transactional
    public Hash getHash() {
        manageCache();
        return hashQueue.remove();
    }

    public void manageCache() {
        if (hashQueue.size() < (double) cacheCapacity * minCapacityPercent) {
            if (isFilling.compareAndSet(false, true)) {
                taskExecutor.execute(() -> populateCache());
                log.info("Sent to executor asynchronously, but now in thread {}", Thread.currentThread());
                hashGenerator.generateBatchAsync();
            }
        }
    }

    public void populateCache() {
        log.info("Populating cache asynchronously {}", Thread.currentThread());
        int batch = cacheCapacity - hashQueue.size();
        hashQueue.addAll(hashRepository.getHashBatch(batch));
        isFilling.set(false);
    }
}