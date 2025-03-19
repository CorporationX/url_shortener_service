package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${spring.cache.capacity}")
    private int capacity;
    @Value("${spring.cache.max-item-count}")
    private int maxCount;
    @Value("${spring.cache.warning-percent-border}")
    private int warningPercentBorder;
    private final Set<Hash> cachedHashes = ConcurrentHashMap.newKeySet(capacity);
    private final Semaphore hashesPullUpSemaphore = new Semaphore(1);

    @Transactional
    public Hash getHash() {
        int warningBorderValue = (int) Math.round((double) maxCount * warningPercentBorder / 100.0);
        if (cachedHashes.size() <= warningBorderValue) {
            if (hashesPullUpSemaphore.tryAcquire()) {
                hashesPullUp();
                hashGenerator.generateBatch();
                hashesPullUpSemaphore.release();
            }
        }
        if (cachedHashes.size() < 1) {
            try {
                hashesPullUpSemaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                hashesPullUpSemaphore.release();
            }
        }
        Hash hash = cachedHashes.stream().findFirst().get();
        cachedHashes.remove(hash);
        return hash;
    }

    @Async("pullUpHashPool")
    protected void hashesPullUp() {
        int countOfNeeded = maxCount - cachedHashes.size();
        if (countOfNeeded < 1) return;
        List<Hash> hashes = hashRepository.findAllWithLimit(countOfNeeded);
        hashRepository.deleteAll(hashes);
        cachedHashes.addAll(hashes);
    }
}
