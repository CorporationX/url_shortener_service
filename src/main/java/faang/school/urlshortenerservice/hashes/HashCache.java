package faang.school.urlshortenerservice.hashes;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository repository;
    private final HashGenerator hashGenerator;
    private final LinkedBlockingQueue<String> hashCache = new LinkedBlockingQueue<>();
    private AtomicBoolean isFilling = new AtomicBoolean(false);
    private final ExecutorService hashCacheThreadPool;

    @Value("${hash.cache_size}")
    private int cacheSize;

    @Value("${hash.min_percent}")
    private int minPercent;

    @PostConstruct
    public void init() {
        hashGenerator.generateBatch();
        List<String> hashes = repository.getHashBatch(cacheSize);
        hashCache.addAll(hashes);
        log.info("Primary hash generation hashes been completed successfully");
    }

    public String getHash() {
        if (needsFilling()) {
            CompletableFuture.runAsync(() -> {
                getMoreHashes();
                log.info("Loaded more hashes to cache");
                isFilling.set(false);
            }, hashCacheThreadPool);
        }
        return hashCache.poll();
    }

    private boolean needsFilling() {
        int cachePercent = (hashCache.size() * 100) / cacheSize;
        return cachePercent < cacheSize
                && !isFilling.compareAndExchange(false, true);
    }

    @Transactional
    @Async("hashCacheThreadPool")
    public void getMoreHashes() {
        int sizeBatch = hashCache.remainingCapacity();
        hashGenerator.generateBatch();
        hashCache.addAll(repository.getHashBatch(sizeBatch));
    }
}