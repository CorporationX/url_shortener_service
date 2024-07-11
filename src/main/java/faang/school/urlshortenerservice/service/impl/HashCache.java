package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {

    @Value("${services.hash.cache-size}")
    private int cacheSize;
    @Value("${services.hash.fill_percent}")
    private int fillPercent;
    private final AtomicBoolean filling = new AtomicBoolean(false);
    private Queue<String> hashes;
    private final HashService hashService;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(cacheSize);
        hashes.addAll(hashService.getHashes(Long.valueOf(cacheSize)));
    }

    public String getHash() {
        if (hashes.size() * 100.0 / cacheSize < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                hashService.getHashesAsync(Long.valueOf(cacheSize))
                        .thenAccept(hashes::addAll)
                        .thenRun(() -> filling.set(false));
            }
        }
        return hashes.poll();
    }

    public void addHashes(List<String> newHashes) {
        hashes.addAll(newHashes);
    }
}
