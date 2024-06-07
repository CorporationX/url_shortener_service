package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private final HashGenerator hashGenerator;

    @Value("${cache.size:10000}")
    private int size;

    @Value("${cache.percentSize}")
    private int percentSize;

    private AtomicBoolean workUpdate = new AtomicBoolean(false);


    private final Queue<String> hashes;

    public HashCache(@Value("${cache.size:10000}") int size, HashGenerator hashGenerator) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        this.hashes = new ArrayBlockingQueue<>(size);
        this.hashGenerator = hashGenerator;
    }

    @PostConstruct
    public void init() {
        hashes.addAll(hashGenerator.getHashes(size));
    }

    @Async("urlShortenerPool")
    public void updateCache() {
        int actualSizeCache = hashes.size() / (size / 100);
        if (actualSizeCache < percentSize && workUpdate.compareAndSet(false, true)) {
            hashGenerator.getHashesAsync(size).thenAccept(hashes::addAll).thenRun(() -> workUpdate.set(false));
        }
    }

    public String getHash() {
        updateCache();
        return hashes.poll();
    }
}
