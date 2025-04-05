package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashCacheProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashQueueManager {
    private final int PERCENT_DIVISOR = 100;
    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);
    private final HashRepository hashRepository;
    private final HashCacheProperties properties;

    public String pollHash() {
        return hashQueue.poll();
    }

    public boolean shouldRefill() {
        return hashQueue.size() < calculateThreshold();
    }

    @Transactional
    public void refillQueueFromData() {
        if (isRefilling.compareAndSet(false, true)) {
            try {
                int needed = Math.max(0, properties.getMaxSize() - hashQueue.size());
                if (needed > 0) {
                    List<String> hashes = hashRepository.getHashBatch(needed);
                    hashQueue.addAll(hashes);
                }
            } finally {
                isRefilling.set(false);
            }
        }
    }

    private int calculateThreshold() {
        return (properties.getMaxSize() * properties.getRefillThresholdPercent()) / PERCENT_DIVISOR;
    }
}
