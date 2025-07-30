package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.hash.HashConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    private final HashService hashService;
    private final HashConfig hashConfig;
    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean blocked = new AtomicBoolean(false);
    private final static String STORAGE_TYPE = "cache";

    public String getHashFromQueue() {
        try {
            refillCache();
            return hashQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void getFreeHashes() {
        List<String> freeHashesList = hashService.getFreeHashes();
        hashQueue.addAll(freeHashesList);
    }

    public void refillCache() {
        boolean checkHashSize = (hashQueue.size() < hashConfig.getCurrentOccupancyCache())
                && blocked.compareAndSet(false, true);

        if (checkHashSize) {
            hashService.refillHashAsync(this::refillCache, STORAGE_TYPE, blocked);
        }
    }
}
