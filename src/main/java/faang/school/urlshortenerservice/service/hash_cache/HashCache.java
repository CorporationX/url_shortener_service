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

    @PostConstruct
    @Transactional
    public CompletableFuture<Void> fillCash() {
        int batchSize = queueProp.getMaxQueueSize() -
                (queueProp.getMaxQueueSize() / 100) * queueProp.getPercentageToStartFill();
        log.info("Start filling local cache with {} elements", batchSize);

        List<String> hashes = hashRepository.getHashBatch(batchSize);
        log.info("Got {} hashes from hash repository", hashes.size());

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<List<String>> subBatches = util.getBatches(hashes, queueProp.getFillingBatchesQuantity());
        subBatches.forEach(batch -> futures.add(CompletableFuture.runAsync(() ->
                localHashCache.addAll(batch), threadPool.hashCacheFillExecutor())));

        hashGenerator.generateBatchHashes(queueProp.getMaxQueueSize());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Transactional
    public String getHash() {
        if (isNecessaryToFill()) {
            if (!isFilling.get()) {
                isFilling.compareAndSet(false, true);
                CompletableFuture<Void> future = CompletableFuture.runAsync(this::fillCash,
                        threadPool.hashCacheFillExecutor());
                threadPool.hashCacheFillExecutor().execute(() -> waitForFillEnding(future));
            }
        }
        String hash = localHashCache.poll();
        log.info("Hash {} was got from local cache", hash);
        return hash;
    }

    private void waitForFillEnding(CompletableFuture<Void> future) {
        future.join();
        isFilling.set(false);
        log.info("Finished filling local cache");
    }

    private boolean isNecessaryToFill() {
        return localHashCache.size() < (queueProp.getMaxQueueSize() / 100) * queueProp.getPercentageToStartFill();
    }
}
