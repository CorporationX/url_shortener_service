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
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final ExecutorService executor;
    private final HashRepository hashRepository;
    private final ConcurrentLinkedQueue<Hash> queue = new ConcurrentLinkedQueue<>();

    @Value("${thread-pool.cache.percentage}")
    private double percentageToAdd;
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;
    private int hashesQuantityToAdd = (int) (batchSize * ((percentageToAdd / 100) * percentageToAdd));

    public Hash getHash() {
        if (queue.size() < hashesQuantityToAdd) {
            synchronized (queue) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    List<Hash> hashes = hashRepository.getHashBatch(batchSize);
                    queue.addAll(hashes);
                }, executor);
                if (queue.isEmpty()) {
                    future.join();
                }
            }
        }
        return queue.poll();
    }
}
