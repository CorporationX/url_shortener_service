package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.cache.capacity:10000}")
    private final int capacity;

    @Value("${hash.cache.threshold:0.2}")
    private final double threshold;

    private final Queue<Hash> hashesQueue = new ArrayBlockingQueue<>(capacity);

    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    void init() {
        hashGenerator.generateBatch();
        List<Hash> hashes = hashRepository.getHashBatch(capacity);
        hashesQueue.addAll(hashes);
    }

    @Transactional
    @Async("myThreadPool")
    public CompletableFuture<String> getHash() {
        if (hashesQueue.size() < (capacity * threshold)
                && filling.compareAndSet(false, true)) {
            hashesQueue.addAll(hashRepository.getHashBatch(capacity - hashesQueue.size()));
            filling.set(false);
        }
        return CompletableFuture.completedFuture(Objects.requireNonNull(hashesQueue.poll()).getHashValue());
    }
}
