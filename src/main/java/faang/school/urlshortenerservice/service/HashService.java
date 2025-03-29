package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.utils.HashCache;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashService {
    private final ExecutorService executorService;
    private final HashCache hashCache;

    @Value("${hash.cache.capacity}")
    private int capacity;

    @Value("${hash.cache.refill_threshold}")
    private double refillThreshold;

    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    public String getFreeHash() {
        String hash = hashCache.getHash();
        if (needRefill()) {
            scheduleRefill();
        }
        return hash;
    }

    public void scheduleRefill() {
        if (isRefilling.compareAndSet(false, true)) {

            try {
                executorService.execute(hashCache::refillHashes);
            } finally {
                isRefilling.set(false);
            }
        }
    }

    private boolean needRefill() {
        return hashCache.getCapacity() < capacity * refillThreshold;
    }
}
