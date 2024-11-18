package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.exception.ServiceException;
import faang.school.urlshortenerservice.generate.HashGenerator;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hash.batch.size}")
    private int batchSize;

    @Value("${hash.percent}")
    private double percent;

    private final ThreadPoolTaskExecutor asyncExecutor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private BlockingQueue<Hash> caches;
    private AtomicBoolean closed = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        caches = new ArrayBlockingQueue<>(batchSize);
        hashGenerator.generateBatch();
        caches.addAll(hashRepository.getHashBatch(batchSize));
        log.info("Hash cache initialized");
    }

    public Hash getHash() {
        if (caches.size() <= batchSize * percent) {
            if (closed.compareAndSet(false, true)) {
                CompletableFuture.runAsync(() -> {
                    try {
                        hashGenerator.generateBatch();
                        caches.addAll(hashRepository.getHashBatch(batchSize));
                    } catch (Exception e) {
                        throw new ServiceException("Exception while generating hashes: " + e.getMessage(), e.getCause());
                    } finally {
                        log.info("Hash generation is complete.Size cache: {}", caches.size());
                        closed.set(false);
                    }
                }, asyncExecutor);
            }
        }
        try {
            return caches.take();
        } catch (InterruptedException e) {
            throw new ServiceException("Something gone wrong while waiting for hash: " + e.getMessage(), e.getCause());
        }
    }
}
