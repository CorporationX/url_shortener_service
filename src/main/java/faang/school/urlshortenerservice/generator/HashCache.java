package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.exception.NoCacheFoundException;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

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
        cache = new LinkedBlockingDeque<>(maxCacheSize);

        // Проверка и загрузка хешей при инициализации
        if (hashRepository.totalNumOfHashesInDb() < hashesForDb * lowThreshold) {
            log.info("Generating hashes due to insufficient hashes in DB.");
            hashGenerator.generateHashes();
        }

        loadHashesFromDb();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledInit() {
        init();
    }

    public String getHashFromCache() {
        String hash = cache.poll();
        if (hash == null || cache.size() < (int)(maxCacheSize * lowThreshold)) {
            loadHashesFromDb();
            hash = hashRepository.getFirstHashAndDeleteFromDb();
            if (hash == null) {
                throw new NoCacheFoundException("Error getting hash from DB");
            }
        }
        return hash;
    }

    private void loadHashesFromDb() {
        // Загружаем хеши, если их недостаточно в кэше и блокируем доступ для предотвращения гонки
        if (cache.size() < (int)(maxCacheSize * lowThreshold) && lock.tryLock()) {
            try {
                List<String> hashesFromDb = hashRepository.getHashBatchAndDeleteFromDb();
                cache.addAll(hashesFromDb);
            } finally {
                lock.unlock();
            }
        }
    }
}
