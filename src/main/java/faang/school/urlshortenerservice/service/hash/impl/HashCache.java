package faang.school.urlshortenerservice.service.hash.impl;

import faang.school.urlshortenerservice.exception.HashNotExistException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.service.hash.HashGenerator;
import faang.school.urlshortenerservice.service.hash.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    private static final String MESSAGE = "Queue is empty, waiting for hash to be cached...";

    private final Queue<Hash> queue = new ConcurrentLinkedQueue<>();
    private final UrlShortenerProperties properties;
    private final ExecutorService executorService;
    private final HashService hashService;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        refillCacheIfNeeded();
    }

    public Hash getHash() {
        executorService.submit(this::refillCacheIfNeeded);

        Hash poll = queue.poll();
        if (poll == null) {
            log.info(MESSAGE);
            throw new HashNotExistException(MESSAGE);
        }
        return poll;
    }

    public synchronized void refillCacheIfNeeded() {
        log.info("Start refillCacheIfNeeded");

        if (getPercentOfFullQueue() < properties.getMinimumHashLengthInPercent()) {
            List<Hash> newHashes = hashService.getBatchHashesAndDelete(properties.getBatchSize());
            Collections.shuffle(newHashes);
            queue.addAll(newHashes);
            log.info("Cache is refilled, size of new hashes: {}", newHashes.size());
            hashGenerator.generateBatch();
        }

        log.info("Finish refillCacheIfNeeded");
    }

    private double getPercentOfFullQueue() {
        double max = properties.getBatchSize();
        double percentOfFullQueue = (double) queue.size() / max * 100;
        log.info("Size of Queue: {}%", percentOfFullQueue);
        return percentOfFullQueue;
    }
}
