package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final Executor hashCacheThreadPool;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Value("${hash.local-cache.cache-size}")
    private int cacheSize;

    @Value("${hash.local-cache.max-fill-threshold}")
    private int thresholdPercent;

    @Value("${hash.cache.batch.size}")
    private int batchSize;

    private final BlockingQueue<String> hashQueue = new LinkedBlockingQueue<>(cacheSize);
    private final AtomicBoolean isRefilling = new AtomicBoolean(false);

    public String getHash() throws InterruptedException {
        if (shouldRefill()) {
            refillCache();
        }
        return hashQueue.take();
    }

    private boolean shouldRefill() {
        int currentSize = hashQueue.size();
        int threshold = cacheSize * thresholdPercent / 100;
        return currentSize <= threshold && isRefilling.compareAndSet(false, true);
    }

    private void refillCache() {
        hashCacheThreadPool.execute(() -> {
            try {
                log.info("Пополнение кэша...");
                List<String> hashes = hashRepository.getHashBatch(batchSize);
                hashQueue.addAll(hashes);
                log.info("Добавлено {} хэшей в кэш.", hashes.size());

                hashCacheThreadPool.execute(() -> {
                    log.info("Генерация новых хэшей в БД...");
                    hashGenerator.generateBatch();
                });
            } catch (DataAccessException e) {
                log.error("Ошибка доступа к данным при пополнении кэша", e);
            } catch (Exception e) {
                log.error("Ошибка при пополнении кэша", e);
            } finally {
                isRefilling.set(false);
            }
        });
    }
}
