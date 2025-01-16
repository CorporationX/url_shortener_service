package faang.school.urlshortenerservice.service.hash_cache;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.properties.HashCacheQueueProperties;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.service.generator.HashGenerator;
import faang.school.urlshortenerservice.util.Util;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {

    private final HashCacheQueueProperties queueProp;
    private final HashRepositoryImpl hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPool threadPool;
    private final Util util;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private Queue<String> localHashCache;

    @PostConstruct
    public void init() {
        localHashCache = new LinkedBlockingQueue<>(queueProp.getMaxQueueSize());
    }

    @Transactional
    public CompletableFuture<Void> fillCache() {
        int batchSize = getBatchSize();
        log.info("Start filling local cache with {} elements", batchSize);

        List<String> hashes = hashRepository.getHashBatch(batchSize);
        log.info("Got {} hashes from hash repository", hashes.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<String>> subBatches = util.getBatches(hashes, queueProp.getFillingBatchesQuantity());
        subBatches.forEach(batch -> futures.add(CompletableFuture.runAsync(() ->
                localHashCache.addAll(batch), threadPool.hashCacheFillExecutor())));

        generateHashes(batchSize);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Transactional
    public String getHash() {
        if (isNecessaryToFill()) {
            if (!isFilling.get()) {
                isFilling.compareAndSet(false, true);

                CompletableFuture.runAsync(() -> fillCache().thenRun(() -> {
                    isFilling.set(false);
                    log.info("Finished filling local cache");
                }), threadPool.hashCacheFillExecutor());
            }
        }
        String hash = localHashCache.poll();
        log.info("Hash {} was got from local cache", hash);
        return hash;
    }

    private boolean isNecessaryToFill() {
        return localHashCache.size() < getPercentageToFill();
    }

    private int getBatchSize() {
        return queueProp.getMaxQueueSize() - getPercentageToFill();
    }

    private int getPercentageToFill() {
        return (queueProp.getMaxQueueSize() / 100) * queueProp.getPercentageToStartFill();
    }

    private void generateHashes(int batchSize) {
        if (hashRepository.getHashesCount() < queueProp.getCountToStopGenerate()) {
            hashGenerator.generateBatchHashes(batchSize);
        }
    }
}
