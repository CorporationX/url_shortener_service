package faang.school.urlshortenerservice.hashes;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("hashCachePool")
    private ExecutorService pool;

    @Value("${hash.cache_size}")
    private int cacheSize;

    @Value("${hash.min_percent}")
    private int minPercent;

    @PostConstruct
    public void init() {
        hashGenerator.generateBatch().join();
        List<String> hashes = repository.getHashBatch(cacheSize);
        hashCache.addAll(hashes);
    }

    @Transactional
    @Async("hashCachePool")
    public String getHash() {
        if (needsFilling()) {
            CompletableFuture.runAsync(() -> {
                getMoreHashes();
                log.info("Loaded more hashes to cache");
                isFilling.set(false);
            }, pool);
        }
        String hash = hashCache.poll();
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Cache is Empty");
        }
        return hash;
    }

    private boolean needsFilling() {
        int cachePercent = (hashCache.size() * 100) / cacheSize;
        return cachePercent < cacheSize
                && !isFilling.compareAndExchange(false, true);
    }

    private void getMoreHashes() {
        int sizeBatch = hashCache.remainingCapacity();
        hashGenerator.generateBatch();
        hashCache.addAll(repository.getHashBatch(sizeBatch));
    }
}
