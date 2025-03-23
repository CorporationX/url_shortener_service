package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashLocalCache {

    private final ShortenerProperties shortenerProperties;
    private final HashService hashService;
    private final BlockingQueue<Hash> hashes;
    private final Executor asyncTaskExecutor;
    private final AtomicBoolean canUpdateHashes = new AtomicBoolean(true);
    private final int minPercentage;
    private final int queueSize;
    private final int minThresholdSize;

    @Autowired
    public HashLocalCache(ShortenerProperties shortenerProperties,
                          HashService hashService,
                          Executor asyncTaskExecutor) {
        if (shortenerProperties == null) {
            throw new IllegalStateException("ShortenerProperties can't be null");
        }
        this.shortenerProperties = shortenerProperties;
        this.hashService = hashService;
        this.asyncTaskExecutor = asyncTaskExecutor;

        hashes = new ArrayBlockingQueue<>(shortenerProperties.queueSize());
        minPercentage = shortenerProperties.minArrayHashPercentage();
        queueSize = shortenerProperties.queueSize();
        minThresholdSize = queueSize * minPercentage / 100;
    }

    @PostConstruct
    public void init() {
        System.out.println("Hash local cache initialization");
        hashes.addAll(hashService.readFreeHashes(shortenerProperties.queueSize()));
    }

    public Hash getFreeHashFromQueue() {
        log.info("Get free hash from queue");
        Hash hash;

        try {
            hash = hashes.remove();
        } catch (NoSuchElementException e) {
            log.info("Alert! Need to fill queue urgently!");
            hashService.readFreeHashes(1);
            hash = hashes.poll();
        }

        log.info("Free hashes: {}/{}, threshold: {}", hashes.size(), queueSize, minThresholdSize);
        if (hashes.size() < minThresholdSize) {
            if (canUpdateHashes.compareAndSet(true, false)) {
                log.info("Current hash queue size {} less than minimum percentage {}", hashes.size(), minPercentage);
                readFreeHashesAsync(queueSize - hashes.size()).thenAccept(hashes::addAll);
            }
        }
        return hash;
    }

    public CompletableFuture<List<Hash>> readFreeHashesAsync(int quantity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return hashService.readFreeHashes(quantity);
            } finally {
                canUpdateHashes.set(true);
            }
        }, asyncTaskExecutor);
    }
}
