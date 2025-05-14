package faang.school.urlshortenerservice.chache;

import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository repository;
    private final HashGenerator hashGenerator;

    @Value("${Hash.cache-size}")
    private final int cacheSize;

    @Value("${Hash.fill-percent}")
    private final int fillPercent;

    private Queue<String> hashes;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @PostConstruct
    public void prepareCache() {
        hashes = new ArrayBlockingQueue<>(cacheSize);
        hashes.addAll(repository.getHashBatch(cacheSize));
    }


    @Async("cachePool")
    public CompletableFuture<String> getHash() {
        if (needRefill() && filling.compareAndSet(false, true)) {
            try {
                getHashBatch(cacheSize - hashes.size());
                if (!isHashesEnough()) {
                    hashGenerator.generateBatch();
                }
            } finally {
                filling.set(false);
            }
        }
        return CompletableFuture.completedFuture(hashes.poll());
    }

    @Transactional
    public List<String> getHashBatch(int size) {
        return repository.getHashBatch(size);
    }

    private boolean isHashesEnough() {
        return (cacheSize < repository.count() / 10);
    }

    private boolean needRefill() {
        return hashes.size() / (cacheSize / 100.0) < fillPercent;
    }

}
