package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashConfig;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashConfig hashConfig;
    private final HashService hashService;


    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean blocked = new AtomicBoolean(false);

    private final static String STORAGE_TYPE = "cache";

    public String takeHash() {
        try {
            if (hashQueue.isEmpty()) {
                log.debug("Nothing to take");
            }
            refillCacheIfDepleted();
            return hashQueue.take();
        } catch (InterruptedException e) {
            log.error("Failed to get hash from cache");
            throw new RuntimeException(e);
        }
    }

    public void refillCache() {
        List<String> freeHashes = hashService.getFreeHashes(hashConfig.getCache().getSize());
        hashQueue.addAll(freeHashes);
    }

    public void refillCacheIfDepleted() {
        boolean depletedAndNotFilling = (hashQueue.size() < hashConfig.getCurrentCacheFullness())
                && blocked.compareAndSet(false, true);

        if (depletedAndNotFilling) {
            log.info("Starting to refill cache");
            hashService.refillStorageFunctionAsync(this::refillCache, STORAGE_TYPE, blocked);
        }
    }
}
