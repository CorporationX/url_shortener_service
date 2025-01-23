package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final BlockingQueue<String> cache;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @Value("${queue-hash-cache.min-fullness-percent}")
    private int minCacheFullnessPercent;

    @Value("${queue-hash-cache.capacity}")
    private int capacity;

    @Autowired
    @Qualifier("getMoreHashesPool")
    private ExecutorService pool;

    @PostConstruct
    public void init() {
        hashGenerator.generateBatch().join();
        List<String> hashes = hashRepository.getHashWithCustomBatch(capacity);
        cache.addAll(hashes);
    }

    public String getFreeHash() {
        if (needsFilling()) {
            CompletableFuture.runAsync(() -> {
                List<String> hashes = getMoreHashes();
                cache.addAll(hashes);
                log.info("Loaded more hashes to cache");
                isFilling.set(false);
            }, pool);
        }
        String hash = cache.poll();
        if (hash == null) {
            log.error("Empty hash cache");
            throw new IllegalStateException("Empty hash cache");
        }
        return hash;
    }

    private boolean needsFilling() {
        int cacheFullnessPercent = (cache.size() * 100) / capacity;
        return cacheFullnessPercent < minCacheFullnessPercent
                && !isFilling.compareAndExchange(false, true);
    }

    private List<String> getMoreHashes() {
        int remainingCapacity = cache.remainingCapacity();
        hashGenerator.generateBatch();
        return hashRepository.getHashWithCustomBatch(remainingCapacity);
    }
}
