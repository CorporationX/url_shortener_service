package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class HashCache {

    private final Executor executor;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final HashProperties hashProperties;

    private final Queue<String> hashQueue;
    private final AtomicBoolean isFetching = new AtomicBoolean(false);

    public HashCache(@Qualifier("hashExecutor") Executor executor,
                     HashRepository hashRepository,
                     HashGenerator hashGenerator,
                     HashProperties hashProperties) {
        this.executor = executor;
        this.hashRepository = hashRepository;
        this.hashGenerator = hashGenerator;
        this.hashProperties = hashProperties;
        this.hashQueue = new ConcurrentLinkedQueue<>();
    }

    @PostConstruct
    public void init() {
        refillCache();
    }

    public String getHash() {
        int capacity = hashProperties.getBatchSize();
        int thresholdPercent = hashProperties.getFillPercent();

        if (hashQueue.size() * 100 / capacity < thresholdPercent) {
            triggerAsyncRefill();
        }

        String hash = hashQueue.poll();

        if (hash == null) {
            refillCache();
            hash = hashQueue.poll();
        }

        return hash;
    }

    private void refillCache() {
        int batchSize = hashProperties.getBatchSize();

        List<Hash> hashes = hashRepository.getHashBatch(batchSize);
        for (Hash h : hashes) {
            if (hashQueue.size() < batchSize) {
                hashQueue.offer(h.getHash());
            } else {
                break;
            }
        }

        log.info("Кэш пополнен. Текущий размер: {}", hashQueue.size());
    }

    private void triggerAsyncRefill() {
        if (isFetching.compareAndSet(false, true)) {
            executor.execute(() -> {
                try {
                    refillCache();
                    hashGenerator.generateBatch();
                } catch (Exception e) {
                    log.error("Ошибка при асинхронном пополнении кэша", e);
                } finally {
                    isFetching.set(false);
                }
            });
        }
    }
}