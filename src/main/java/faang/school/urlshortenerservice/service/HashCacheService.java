package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCacheService {
    @Value("${cache.hash.capacity")
    private final int capacity;
    @Value("${cache.hash.min")
    private final int min;
    @Value("${hash-generation.hash-batch")
    private final int hashBatch;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final BlockingQueue<Hash> queue = new ArrayBlockingQueue<>(capacity);

    private final HashRepository repository;

    public Hash getHash() {
        if(queue.size() < min && isRunning.compareAndSet(false, true)) {
            fillHashCache();
        }
        return queue.poll();
    }

    @Async("Executor")
    private void fillHashCache() {
            List<Hash> hashes = repository.getHashBatch(hashBatch);
            queue.addAll(hashes);
    }
}
