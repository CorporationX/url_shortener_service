package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.properties.HashCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private final int queueSize;
    private final int hashesQuantityToAdd;
    private final ExecutorService threadPoolExecutor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final LinkedBlockingQueue<Hash> queue;
    private AtomicBoolean availableToRefil;

    @Autowired
    public HashCache(@Qualifier("hashCachePool") ExecutorService threadPoolExecutor, HashRepository hashRepository,
                     HashGenerator hashGenerator, HashCacheProperties hashCacheProperties) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;

        this.queueSize = hashCacheProperties.getSize();
        double percentageToAdd = hashCacheProperties.getMinimumPercentageToAdd();
        hashesQuantityToAdd = (int) Math.round((double) queueSize * percentageToAdd);
        queue = new LinkedBlockingQueue<>(queueSize);
        availableToRefil = new AtomicBoolean(true);

        hashGenerator.generateBatch();
    }

    public Hash getHash() {
        if (queue.size() < hashesQuantityToAdd && availableToRefil.get()) {
            availableToRefil.set(false);

            CompletableFuture future = CompletableFuture.runAsync(() -> {
                int batchSize = queueSize - queue.size();
                queue.addAll(hashRepository.getHashBatch(batchSize));
                availableToRefil.set(true);
            }, threadPoolExecutor);
            future.thenRunAsync(hashGenerator::generateBatch, threadPoolExecutor);
        }

        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
