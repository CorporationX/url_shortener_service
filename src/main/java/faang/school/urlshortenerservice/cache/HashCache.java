package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private Queue<String> queue;
    private final Executor taskExecutor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final RAtomicLong fillingFlag;
    private final RLock fillingLock;

    @Setter
    @Value("${hash.cache.capacity}")
    private int cacheCapacity;

    @Setter
    @Value("${hash.cache.min-filling-ratio}")
    private double minFillingRatio;

    @Setter
    @Value("${hash.repository.batch-size}")
    private int batchSize;

    @PostConstruct
    private void init() {
        queue = new ArrayBlockingQueue<>(cacheCapacity);
        fillCache();
    }

    @Async("taskExecutor")
    public CompletableFuture<String> getHash() {
        CompletableFuture<String> result = CompletableFuture.supplyAsync(queue::poll);
        fillingLock.lock();
        if (queue.size() * 1.0 / cacheCapacity <= minFillingRatio && fillingFlag.get() == 0) {
            fillingFlag.set(1);
            taskExecutor.execute(this::fillCache);
        }
        fillingLock.unlock();
        return result;
    }

    private void fillCache() {
        try {
            List<String> hashes = hashRepository.getHashBatch();
            if (hashes.size() < batchSize) {
                hashGenerator.generateBatch();
            }
            queue.addAll(hashes);
            log.info("Added {} hashes to cache", hashes.size());
        } finally {
            fillingFlag.set(0);
        }
    }
}
