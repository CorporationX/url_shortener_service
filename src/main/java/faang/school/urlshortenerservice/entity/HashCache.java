package faang.school.urlshortenerservice.entity;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repo.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final ExecutorService executorService;
    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();

    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.cache.refill-threshold-percent}")
    private int refillThresholdPercent;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        refillCacheAsync();
    }

    public String getHash() {
        if (needToRefill()) {
            refillCacheAsync();
        }
        return hashQueue.poll();
    }

    private boolean needToRefill() {
        return hashQueue.size() < cacheSize * refillThresholdPercent / 100
                && isRefilling.compareAndSet(false, true);
    }

    private void refillCacheAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                int needed = cacheSize - hashQueue.size();
                List<String> hashes = hashRepository.getHashBatch(needed);
                hashQueue.addAll(hashes);

                if (hashes.size() < needed) {
                    generateAdditionalHashesAsync(needed - hashes.size());
                }
            } catch (Exception e) {
                log.error("Error refilling hash cache", e);
            } finally {
                isRefilling.set(false);
            }
        }, executorService);
    }

    private void generateAdditionalHashesAsync(int count) {
        CompletableFuture.runAsync(() -> {
            try {
                List<Long> numbers = hashRepository.getUniqueNumbers(count);
                List<String> hashes = base62Encoder.encode(numbers);
                hashRepository.saveAllHashes(hashes);

                // Добавляем новые хэши сразу в кэш
                hashQueue.addAll(hashes);
            } catch (Exception e) {
                log.error("Error generating additional hashes", e);
            }
        }, executorService);
    }
}