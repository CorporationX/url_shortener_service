package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final LinkedBlockingQueue<String> hashCache = new LinkedBlockingQueue<>();

    private final Lock lock = new ReentrantLock();

    @Value("${hash.min-hash-cache-size:1000}")
    private int minHashCacheSize;

    public String getHash() {
        if (hashCache.size() < minHashCacheSize && lock.tryLock()) {
            getNewHashes();
        }

        try {
            return hashCache.take();
        } catch (InterruptedException e) {
            log.error("Error while taking hash", e);
            throw new RuntimeException(e);
        }
    }

    @Async(value = "hashCacheThreadPool")
    public void getNewHashes() {
        try {
            log.info("Start getting new hashes");
            List<String> hashBatch = hashRepository.getHashBatch();
            hashCache.addAll(hashBatch);
            hashGenerator.generateBatch();
            log.info("Finish getting new hashes");
        } finally {
            lock.unlock();
        }
    }
}
