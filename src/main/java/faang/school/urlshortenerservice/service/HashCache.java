package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class HashCache {

    private static final int safeDelta = 5;

    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;
    private final AtomicBoolean lock = new AtomicBoolean(false);

    private final Long maxAmount;
    private final int minPercent;
    private final Queue<String> hashQueue;

    public HashCache(
        HashGenerator hashGenerator,
        ExecutorService executorService,
        @Value("${app.cache.max-amount:10}") Long maxAmount,
        @Value("${app.min-percent:3}") int minPercent
    ) {
        this.hashGenerator = hashGenerator;
        this.executorService = executorService;
        this.maxAmount = maxAmount;
        this.minPercent = minPercent;
        int capacity = (int) (maxAmount + (maxAmount * minPercent / 100) + safeDelta);
        this.hashQueue = new ArrayBlockingQueue<>(capacity);
    }

    @PostConstruct
    public void init() {
        log.info("Init class HashCache");
        fillCache();
    }

    public String getNewHash() {
        do {
            String hash = hashQueue.poll();
            if (hash == null) {
                if (lock.compareAndExchange(false, true)) {
                    log.info("Redis Hash Cache is empty. Filling the cache Synchronously.");
                    fillCache();
                    lock.set(false);
                }
            } else {
                if (isNeedExtend()) {
                    log.debug("NEED extend cache. lock is: {}", lock.get());
                    if (lock.compareAndSet(false, true)) {
                        log.debug("Filling the cache Asynchronously. before start CompletableFuture.supplyAsync");
                        CompletableFuture.supplyAsync(
                                () -> {
                                    log.debug("Filling the cache Asynchronously. Into process. maxAmount is: {}",
                                        maxAmount);
                                    return hashGenerator.getHashes(maxAmount);
                                }, executorService)
                            .thenAccept(hashes -> {
                                log.debug("Filling the cache Asynchronously. hashes: {}", hashes);
                                addHashPortion(hashes);
                            })
                            .thenRun(() -> {
                                log.debug("before restore flag lock. lock is: {}", lock.get());
                                lock.set(false);
                                log.debug("restored flag lock. lock is: {}", lock.get());
                            });
                    }
                    log.debug("after check lock status. lock is: {}", lock.get());
                } else {
                    log.debug("NO NEED extend cache. lock is: {}", lock.get());
                }
                return hash;
            }
        } while (true);
    }

    protected Queue<String> getHashQueueForTesting() {
        return hashQueue;
    }

    private boolean isNeedExtend() {
        int cacheSize = hashQueue.size();
        log.debug("free hash size: {}", cacheSize);
        return cacheSize < (maxAmount * minPercent / 100);
    }

    private void fillCache() {
        List<String> freeHashes = hashGenerator.getHashes(maxAmount);
        addHashPortion(freeHashes);
    }

    private void addHashPortion(List<String> freeHashes) {
        log.debug("Adding Hash Portion. Portion: {}", freeHashes);
        hashQueue.addAll(freeHashes);
    }
}
