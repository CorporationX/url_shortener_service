package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Slf4j
@RequiredArgsConstructor
@Repository
public class HashCache {
    private final HashService hashService;
    @Value("${hash-generator.local-cache-size:1000}")
    private int cacheSize;
    private Queue<String> cache;
    @Value("${hash-generator.ratio-free-hashes:0.2}")
    private double ratioFreeHashes;
    private volatile boolean isReceivedHash;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        List<String> hashes = hashService.getHashes(cacheSize);
        cache.addAll(hashes);
        log.info("Hash cache initialized");
    }

    @Transactional
    public String getHash() {
        if (((double) cache.size() / cacheSize) < ratioFreeHashes && !isReceivedHash) {
            isReceivedHash = true;
            synchronized (cache) {
                int count = cacheSize - cache.size();
                List<String> hashes = hashService.getHashes(count);
                cache.addAll(hashes);
            }
            isReceivedHash = false;
        }
        return cache.poll();
    }
}
