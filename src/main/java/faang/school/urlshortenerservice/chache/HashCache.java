package faang.school.urlshortenerservice.chache;

import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HashCache {
    private final HashRepository repository;
    private final HashGenerator hashGenerator;
    private final int cacheSize;
    private final int fillPercent;

    private Queue<String> hashes;
    private final AtomicBoolean filling = new AtomicBoolean(false);

    @Autowired
    public HashCache(HashRepository repository,
                     HashGenerator hashGenerator,
                     @Value("${app.hash.cache-size}") int cacheSize,
                     @Value("${app.hash.fill-percent}") int fillPercent) {
        this.repository = repository;
        this.hashGenerator = hashGenerator;
        this.cacheSize = cacheSize;
        this.fillPercent = fillPercent;
        this.hashes = new ArrayBlockingQueue<>(cacheSize);
    }

    @PostConstruct
    public void prepareCache() {
        hashes.addAll(repository.getHashBatch(cacheSize));
    }


    @Async("cachePool")
    public CompletableFuture<String> getHash() {
        if (needRefill() && filling.compareAndSet(false, true)) {
            try {
                getHashBatch(cacheSize - hashes.size());
                hashGenerator.generateBatch();

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


    private boolean needRefill() {
        return hashes.size() / (cacheSize / 100.0) < fillPercent;
    }

}
