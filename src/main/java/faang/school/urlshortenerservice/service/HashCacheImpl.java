package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCacheImpl implements HashCache {

    private final int cacheCapacity;
    private final LinkedBlockingQueue<String> cache;
    private final Executor executor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean refillProcessing = new AtomicBoolean(false);

    @Autowired
    public HashCacheImpl(@Value("${app.hash-cache.cache-capacity}") int cacheCapacity, Executor hashCacheExecutor, HashRepository hashRepository, HashGenerator hashGenerator) {
        this.cacheCapacity = cacheCapacity;
        this.cache = new LinkedBlockingQueue<>(cacheCapacity);
        this.executor = hashCacheExecutor;
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
    }

    @SneakyThrows
    @Override
    public String getHash() {
        String hash = cache.take();
        if (cache.size() < cacheCapacity * 0.2) { //todo: вынести в конфиг
            if (!refillProcessing.get()) {
                refillAsync();
            }
        }
        return hash;
    }

    private void refillAsync() {
        executor.execute(this::refill);
    }

    private void refill() {
        refillProcessing.set(true);
        List<String> hashes = hashRepository.getHashBatch(100L); //todo
        cache.addAll(hashes);
        refillProcessing.set(false);
        hashGenerator.generateBatch();
    }

    @PostConstruct
    private void initiateCache() {
        refill();
    }
}
