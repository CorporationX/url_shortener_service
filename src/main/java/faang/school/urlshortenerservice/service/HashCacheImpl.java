package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class HashCacheImpl implements HashCache {

    private final int cacheCapacity;
    private final int fillPercent;
    private final LinkedBlockingQueue<String> cache;
    private final Executor executor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean refillProcessing = new AtomicBoolean(false);

    @Autowired
    public HashCacheImpl(@Value("${app.hash-cache.cache-capacity:1000}") int cacheCapacity,
                         @Value("${app.hash-cache.fill-percent:20}") int fillPercent,
                         Executor hashCacheExecutor,
                         HashRepository hashRepository,
                         HashGenerator hashGenerator) {

        this.cacheCapacity = cacheCapacity;
        this.cache = new LinkedBlockingQueue<>(cacheCapacity);
        this.fillPercent = fillPercent;
        this.executor = hashCacheExecutor;
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
    }

    @PostConstruct
    private void initCache() {

        refill();
    }

    @SneakyThrows
    @Override
    public String getHash() {
        boolean fillRequired = cache.size() < cacheCapacity * (fillPercent / 100.0);
        if (fillRequired) {
            if (refillProcessing.compareAndSet(false, true)) {
                log.info("Remaining capacity is under {}%, starting async task to refill", fillPercent);
                refillAsync();
            }
        }
        return cache.poll();
    }

    private void refillAsync() {

        executor.execute(this::refill);
    }

    //@Transactional todo
    private void refill() {
        int numberToRefill = cache.remainingCapacity();
        log.info("Refilling cache from DB for {} hashes", numberToRefill);
        List<String> hashes = hashRepository.pollHashBatch(numberToRefill);
        cache.addAll(hashes);
        refillProcessing.set(false);
        hashGenerator.generateBatchIfNeeded();
    }
}
