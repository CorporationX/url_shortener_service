package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;
    private final ThreadPoolTaskExecutor taskExecutor;

    private double lowCacheSize;
    private AtomicBoolean isFilling;
    private BlockingQueue<Hash> blockingQueue;


    @PostConstruct
    public void setUp() {
        isFilling = new AtomicBoolean(false);
        blockingQueue = new ArrayBlockingQueue<>(hashProperties.getCapacity(), true);
        lowCacheSize = hashProperties.getCapacity() * hashProperties.getLowSizePercentage() / 100.0;

        log.info("Initial cache filling.");
        refillCache();
    }

    public Hash getHash() {
        if (blockingQueue.size() < lowCacheSize && !isFilling.get()) {
            log.warn("The hash cache size lowed below {} percent.", hashProperties.getLowSizePercentage());
            refillCache();
        }

        return blockingQueue.poll();
    }

    private void refillCache() {
        isFilling.set(true);

        hashGenerator.generateBatch();
        taskExecutor.execute(() -> {
            blockingQueue.addAll(hashService.getHashBatch());
            isFilling.set(false);
            log.info("The hash cache filled and it's current size is {} hashes.", blockingQueue.size());
        });
    }
}
