package faang.school.urlshortenerservice.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final AsyncExecutorForHashCash asyncEx;

    @Value("${hash.cache.size}")
    private int maxCacheSize;
    @Value("${hash.cache.min_threshold}")
    private int minThreshold;
    @Value("${spring.data.batch_size}")
    private int batchSize;

    private final ConcurrentLinkedQueue<String> hashCache = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean exclAccessAllowed = new AtomicBoolean(false);

    @Transactional
    public String getHash() {
        if(hashCache.size() < minThreshold * maxCacheSize / 100) {
            log.info("HashCash's size is {}. This value is less than {}% of {} ",
                    hashCache.size(), minThreshold, maxCacheSize);
            if (exclAccessAllowed.compareAndSet(false, true)) {
                asyncEx.asyncGenerateBatch();
                asyncEx.exclusiveTransferHashBatch(batchSize, hashCache);
                exclAccessAllowed.set(false);
            }
        }
        String hash = hashCache.poll();
        log.info("Hash {} got from cache", hash);
        return hash;
    }
}
