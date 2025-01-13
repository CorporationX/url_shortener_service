package faang.school.urlshortenerservice.service.hash_cashe;

import faang.school.urlshortenerservice.config.async.ThreadPool;
import faang.school.urlshortenerservice.properties.HashCashQueueProperties;
import faang.school.urlshortenerservice.repository.hash.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.service.generatr.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@DependsOn("hashGenerator")
public class HashCache {

    private final HashCashQueueProperties queueProp;
    private final HashRepositoryImpl hashRepositoryImpl;
    private final HashGenerator hashGenerator;
    private final ThreadPool threadPool;

    private final Queue<String> queue = new LinkedBlockingQueue<>(queueProp.getMaxQueueSize());
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @PostConstruct()
    @Transactional
    @Async(value = "hashCacheFillExecutor")
    public CompletableFuture<Void> fillCash() {
        int batchSize = queueProp.getMaxQueueSize() -
                (queueProp.getMaxQueueSize() / 100) * queueProp.getPercentageToStartFill();

        List<Integer> subBatches = getSubBatches(batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        subBatches.forEach(batch -> futures.add(CompletableFuture.runAsync(() ->
                        queue.addAll(hashRepositoryImpl.getHashBatch(batch))
                , threadPool.hashCacheFillExecutor())));

        hashGenerator.generateBatch(batchSize);
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Transactional
    public String getHash() {
        if (isNecessaryToFill()) {
            if (!isFilling.get()) {
                isFilling.compareAndSet(false, true);
                CompletableFuture<Void> future = fillCash();
                future.join();
                isFilling.set(false);
            }
        }
        return queue.poll();
    }

    private List<Integer> getSubBatches(int batchSize) {
        List<Integer> subBatches = new ArrayList<>();
        int subBatchesQuantity = queueProp.getFillingBatchesQuantity();
        int baseBatchSize = batchSize / subBatchesQuantity;
        int remainder = batchSize % subBatchesQuantity;

        for (int i = 0; i < subBatchesQuantity; i++) {
            int currentBatchSize = baseBatchSize + (i < remainder ? 1 : 0);
            subBatches.add(currentBatchSize);
        }
        return subBatches;
    }

    private boolean isNecessaryToFill() {
        return queue.size() < (queueProp.getMaxQueueSize() / 100) * queueProp.getPercentageToStartFill();
    }
}
