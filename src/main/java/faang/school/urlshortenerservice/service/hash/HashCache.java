package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private BlockingQueue<Hash> cache;

    @Value("${hash.cache.capacity}")
    private int cacheCapacity;
    @Value("${hash.cache.low-size-percentage}")
    private int lowSizePercentage;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);


    @PostConstruct
    public void setUp() {
        cache = new ArrayBlockingQueue<>(cacheCapacity, true);

        log.info("Initial cache filling.");
        refillCache();
    }

    //TODO: использовать в будущих задачах
    public Hash getHash() {
        if (cache.size() < cacheCapacity * lowSizePercentage / 100 && !isFilling.get()) {
            log.warn("The hash cache size lowed below {} percent and refilling process was initialized.", lowSizePercentage);
            refillCache();
        }

        return cache.poll();
    }

    private void refillCache() {
        isFilling.set(true);

        hashGenerator.generateBatch();
        taskExecutor.execute(() -> {
            cache.addAll(hashRepository.getHashBatch());
            isFilling.set(false);
            log.info("The hash cache filled and it's current size is {} hashes.", cache.size());
        });
    }
}