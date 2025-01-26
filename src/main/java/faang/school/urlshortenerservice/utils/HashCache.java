package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.exceptions.NoCacheFoundException;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${hashGenerator.batchSize}")
    private int hashesForDb;

    @Value("${hashCache.lowThreshold}")
    private double lowThreshold;

    @Value("${hashCache.maxSize}")
    private int maxCacheSize;

    private final Lock lock = new ReentrantLock();

    private LinkedBlockingDeque<String> cache;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Initializing HashCache with batchSize: {}, lowThreshold: {}, maxSize: {}", hashesForDb, lowThreshold, maxCacheSize);
        int percentOfTotal = (int) (hashesForDb * lowThreshold);
        cache = new LinkedBlockingDeque<>(maxCacheSize);

        if (isRunning.compareAndSet(false, true)) {
            try {
                if (hashRepository.totalNumOfHashesInDb() == null || hashRepository.totalNumOfHashesInDb() < percentOfTotal) {
                    log.info("Generating hashes because totalNumOfHashesInDb is less than threshold.");
                    hashGenerator.generateHashes();
                    loadHashesFromDb();
                }
            } catch (Exception e) {
                log.error("Error during HashCache initialization", e);
            } finally {
                isRunning.set(false);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledInit() {
        init();
    }

    public String getHashFromCache() {
        String hash = cache.poll();
        if (cache.size() < (int) (maxCacheSize * lowThreshold)) {
            loadHashesFromDb();
        }
        if (hash != null) {
            return hash;
        } else {
            loadHashesFromDb();
            try {
                return hashRepository.getFirstHashAndDeleteFromDb();
            } catch (Exception e) {
                throw new NoCacheFoundException("Error getting hash from DB" + e.toString());
            }
        }
    }

    private void loadHashesFromDb() {
        if (cache.size() < (int) (maxCacheSize * lowThreshold) && lock.tryLock()) {
            try {
                List<String> hashesFromDb = hashRepository.getHashBatchAndDeleteFromDb();
                cache.addAll(hashesFromDb);
            } finally {
                lock.unlock();
            }
        }
    }
}
