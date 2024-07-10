package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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
    private final HashProperties hashProperties;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor taskExecutor;
    private BlockingQueue<Hash> cache;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private double lowCacheSize;


    @PostConstruct
    public void setUp() {
        lowCacheSize = hashProperties.getCache().getCapacity() * hashProperties.getCache().getLowSizePercentage() / 100.0;
        cache = new ArrayBlockingQueue<>(hashProperties.getCache().getCapacity(), true);

        log.info("Initial cache filling.");
        refillCache();
    }

    public Hash getHash() {
        if (cache.size() < lowCacheSize && !isFilling.get()) {
            log.warn(
                    "The hash cache size lowed below {} percent and refilling process was initialized.",
                    hashProperties.getCache().getLowSizePercentage()
            );
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