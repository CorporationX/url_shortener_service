package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.service.impl.UrlShortenerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hash.batch.size}")
    private int batchSize;

    @Value("${hash.range}")
    private int maxRange;

    @Value("${hash.percent}")
    private double percent;

    private final Executor asyncExecutor;
    private final UrlShortenerServiceImpl urlShortenerService;
    private final BlockingQueue<Hash> caches = new ArrayBlockingQueue<>(batchSize);
    private AtomicBoolean closed = new AtomicBoolean(false);

    public Hash getHash() {
        if (caches.size() <= batchSize * percent && closed.compareAndSet(false, true)) {
            asyncExecutor.execute(() -> {
                try {
                    caches.addAll(urlShortenerService.getHashes(batchSize));
                    urlShortenerService.putNewHash(maxRange);
                } catch (Exception e) {
                    throw new DataValidationException("Exception " + e.getMessage());
                } finally {
                    closed.set(false);
                }
            });
        }
        try {
            return caches.take();
        } catch (InterruptedException e) {
            throw new DataValidationException("Something gone wrong while waiting for hash" + e.getMessage());
        }
    }
}
