package faang.school.urlshortenerservice.utilities;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
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

    private final AtomicBoolean isInProcess = new AtomicBoolean(false);
    private BlockingQueue<Hash> processedCaches;

    private final HashRepository repositoryOfHashes;
    private final HashGenerator hashesGenerator;

    @PostConstruct
    public void init() {
        processedCaches = new ArrayBlockingQueue<>(capacity);
        processedCaches.addAll(hashesGenerator.getHashes(capacity));
    }

    @Transactional
    public Hash getHash() {
        if(processedCaches.size() < min && isInProcess.compareAndSet(false, true)) {
            fillHashCache();
        }
        return processedCaches.poll();
    }

    @Async("Executor")
    public void fillHashCache() {
        List<Hash> hashList = repositoryOfHashes.getHashBatch(hashBatch);
        this.processedCaches.addAll(hashList);
        hashesGenerator.generateBatch();
    }
}
