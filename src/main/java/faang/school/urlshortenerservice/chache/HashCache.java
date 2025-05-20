package faang.school.urlshortenerservice.chache;

import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCache {
    private final HashRepository repository;
    private final HashGenerator hashGenerator;
    private final int cacheSize;
    private final int fillPercent;
    private Queue<String> hashes;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @Autowired
    public HashCache(HashRepository repository,
                     HashGenerator hashGenerator,
                     @Value("${app.hash.cache-size}") int cacheSize,
                     @Value("${app.hash.fill-percent}") int fillPercent) {
        this.repository = repository;
        this.hashGenerator = hashGenerator;
        this.cacheSize = cacheSize;
        this.fillPercent = fillPercent;
        this.hashes = new ArrayBlockingQueue<>(cacheSize);
    }

    @PostConstruct
    public void prepareCache() {
        if (repository.count() == 0) {
            hashGenerator.generateBatch();
        }
        hashes.addAll(repository.getHashBatch(cacheSize));
    }


    @Async("cachePool")
    public CompletableFuture<String> getHash() {
        if (hashes.isEmpty()) {
            refillCache();
        }

        if (needRefill() && filling.compareAndSet(false, true)) {
            refillCache();
        }

        String hash = hashes.poll();
        if (hash == null) {
            log.warn("Cache is empty, retrying...");
            return getHash().thenCompose(CompletableFuture::completedFuture);
        }
        return CompletableFuture.completedFuture(hash);
    }


    @Transactional
    public void refillCache() {
        try {
            List<String> newHashes = repository.getHashBatch(cacheSize - hashes.size());
            hashes.addAll(newHashes);
            hashGenerator.generateBatch();
            if (newHashes.isEmpty()) {
                hashGenerator.generateBatch();
            }
        } finally {
            filling.set(false);
        }
    }


    private boolean needRefill() {
        return hashes.size() / (cacheSize / 100.0) < fillPercent;
    }

}
