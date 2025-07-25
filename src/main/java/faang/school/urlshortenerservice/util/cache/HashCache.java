package faang.school.urlshortenerservice.util.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${cache.hash.capacity}")
    private int capacity;
    @Value("${cache.hash.min}")
    private int min;
    @Value("${hash-generation.hash-batch}")
    private int hashBatch;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private BlockingQueue<Hash> hashes;

    private final HashRepository repository;
    private final HashGenerator generator;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        hashes.addAll(generator.getHashes(capacity));
    }

    @Transactional
    public Hash getHash() {
        if(hashes.size() < min && isRunning.compareAndSet(false, true)) {
            fillHashCache();
        }
        return hashes.poll();
    }

    @Async("Executor")
    public void fillHashCache() {
        List<Hash> hashes = repository.findAndDelete(hashBatch);
        this.hashes.addAll(hashes);
        generator.generateBatch();
    }
}