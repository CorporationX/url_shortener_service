package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final Executor hashCacheExecutor;

    @Value("${hash.cache.max-size}")
    private int maxSize;

    @Value("${hash.cache.refill-threshold-percent}")
    private int refillThresholdPercent;

    @Value("${hash.cache.batch-size}")
    private int batchSize;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();

    public String getHash(){
        maybeRefill();

        String hash = cache.poll();

        if (hash == null) {
            throw new IllegalStateException("No available hashes in cache");
        }

        return hash;
    }

    private void maybeRefill() {
        int currentSize = cache.size();
        int threshold = maxSize * refillThresholdPercent / 100;

        if(currentSize <= threshold && isRefilling.compareAndSet(false,true)){
            log.info("Hash cache below threshold ({} of {}). Refilling...", currentSize, maxSize);
            hashCacheExecutor.execute(this::refillCacheAsync);
        }
    }

    private void refillCacheAsync() {
        try {
            while (cache.size() <maxSize){
                var batch  = hashRepository.getHashBatch(batchSize);
                if(batch.isEmpty()){
                    log.warn("No hashes available in DB. Triggering generator...");
                    hashGenerator.generateBatch();
                    break;
                }
                cache.addAll(batch);
                log.info("Refilled {} hashes into cache", batch.size());
            }
        } catch (Exception e) {
            log.error("Error while refilling hash cache", e);
        } finally {
            isRefilling.set(false);
        }
    }

    @PostConstruct
    public void init() {
        log.info("Initializing HashCache...");
        maybeRefill();
    }
}
