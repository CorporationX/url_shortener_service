package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Qualifier(value = "HashCachePool")
    private final ThreadPoolTaskExecutor executorService;
    private final HashRepository hashRepository;
    private final ConcurrentLinkedQueue<Hash> queue = new ConcurrentLinkedQueue<>();

    @Value("${thread-pool.cache.percentage}")
    private double percentageToAdd;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    private int hashesQuantityToAdd = (int) (batchSize * ((percentageToAdd / 100) * percentageToAdd));

    public synchronized Hash getHash() {
        if (queue.size() < hashesQuantityToAdd) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<Hash> hashes = hashRepository.getHashBatch(batchSize);
                queue.addAll(hashes);
            }, executorService);
            if (queue.isEmpty()) {
                future.join();
            }
        }
        return queue.poll();
    }
}
