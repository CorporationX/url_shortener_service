package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.hash-cache.min-size:1000}")
    private int minSize;

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashCacheService hashCacheService;
    private final LinkedBlockingQueue<String> hashCache = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();

    @PostConstruct
    public void init() {
        hashGenerator
                .generateBatch()
                .thenRun(() -> hashCache.addAll(hashRepository.getHashBatch()));
    }

    public String getHash() {
        if (hashCache.size() < minSize && lock.tryLock()) {
            hashCacheService.getHashes()
                    .thenAccept(hashCache::addAll)
                    .thenRun(lock::unlock);
        }

        try {
            return hashCache.take();
        } catch (InterruptedException e) {
            log.error("Error occurred while getting hash", e);
            throw new RuntimeException(e);
        }
    }
}
