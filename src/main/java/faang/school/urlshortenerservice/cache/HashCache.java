package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final Queue<String> hashQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private final ExecutorService hashFillingExecutor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.at-least-amount}")
    private int maxHashCache;

    @Value("${hash.max-local-cache}")
    private int maxLocalCache;

    @Value("${hash.min-percent}")
    private int minPercent;

    @Value("${hash.batch-size}")
    int batchSize;

    @Transactional
    public String getHash() {
        if (hashQueue.isEmpty()) {
            loadBatch(maxLocalCache);
        } else if (hashQueue.size() <= minPercent * maxHashCache / 100) {
            if (isRefilling.compareAndSet(false, true)) {
                CompletableFuture.runAsync(this::fillQueue, hashFillingExecutor)
                        .whenComplete((result, error) -> isRefilling.set(false));
            }
        }
        return hashQueue.poll();
    }

    private void fillQueue() {
        hashGenerator.generateHashesBatch((int) (Math.max(0, maxHashCache - hashRepository.count())));

        int requiredAmount = maxLocalCache - hashQueue.size();
        int numThreads = (int) Math.ceil((double) requiredAmount / batchSize);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            futures.add(CompletableFuture.runAsync(() -> loadBatch(batchSize), hashFillingExecutor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void loadBatch(int batchSize) {
        List<Hash> hashes = hashRepository.getHashesBatch(batchSize);
        log.info("Retrieved {} hashes from database", hashes.size());
        hashes.stream()
                .map(Hash::getHashString)
                .forEach(hashQueue::add);
    }
}
