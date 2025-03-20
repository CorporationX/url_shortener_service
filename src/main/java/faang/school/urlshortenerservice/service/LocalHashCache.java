package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.shortener.ShortenerProperties;
import faang.school.urlshortenerservice.model.Hash;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class LocalHashCache {

    private final ShortenerProperties shortenerProperties;
    private final HashService hashService;
    private final BlockingQueue<Hash> hashes;

    private AtomicBoolean canUpdateHashes = new AtomicBoolean(true);

    @Autowired
    public LocalHashCache(ShortenerProperties shortenerProperties, HashService hashService) {
        if (shortenerProperties == null) {
            log.error("ShortenerProperties can't be null");
            throw new IllegalStateException("ShortenerProperties can't be null");
        }
        this.shortenerProperties = shortenerProperties;
        this.hashService = hashService;
        hashes = new ArrayBlockingQueue<>(shortenerProperties.queueSize());
    }

    @PostConstruct
    public void init() {
        System.out.println("Hash local cache initialization");
        hashes.addAll(hashService.readFreeHashes());
    }

    public Hash getFreeHashFromQueue() {
        log.info("Get free hash from queue");
        if (canUpdateHashes.compareAndSet(false, true)) {
            updateHashesIfNeeded();
            canUpdateHashes.set(false);
        }
        return hashes.poll();
    }

    private void updateHashesIfNeeded() {
        int minPercentage = shortenerProperties.minArrayHashPercentage();
        int thresholdSize = shortenerProperties.queueSize() * minPercentage / 100;
        if (hashes.size() < thresholdSize) {
            log.info("Current hash queue size {} less than minimum percentage {}", hashes.size(), minPercentage);
            hashService.readFreeHashesAsync().thenAccept(hashes::addAll);
        }
    }
}
