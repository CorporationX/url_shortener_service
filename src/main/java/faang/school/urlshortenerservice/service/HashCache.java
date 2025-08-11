package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashesRepository;
import faang.school.urlshortenerservice.service.hashgenearator.HashGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class HashCache {
    private final HashesRepository hashesRepository;
    private final HashGenerator hashGenerator;
    private final Executor hashCacheTaskExecutor; // внедряем напрямую
    @Value("${url-shortener-service.hash-cache.hashes-queue.size:100}")
    private long hashesQueueSize;
    @Value("${url-shortener-service.hash-cache.hashes-queue.fill-threshold-percent:20}")
    private long hashesFillThresholdPercent;
    ConcurrentLinkedQueue<String> hashes = new ConcurrentLinkedQueue<>();
    AtomicBoolean isFilling = new AtomicBoolean(false);
    AtomicBoolean ready = new AtomicBoolean(false);

    public HashCache(
            HashesRepository hashesRepository,
            HashGenerator hashGenerator,
            @Qualifier("hashCacheTaskExecutor") Executor hashCacheTaskExecutor
    ) {
        this.hashesRepository = hashesRepository;
        this.hashGenerator = hashGenerator;
        this.hashCacheTaskExecutor = hashCacheTaskExecutor;
    }

    @PostConstruct
    public void init() {
        if (hashesRepository.count() < hashesQueueSize) {
            log.info("Init hashes generation");
            hashGenerator.generateBatch().thenAccept(result -> ready.set(true));
        } else {
            ready.set(true);
        }
    }

    public String getHash() {
        if (hashes.size() < hashesQueueSize * hashesFillThresholdPercent / 100 && !isFilling.get()) {
            hashCacheTaskExecutor.execute(this::fillHashes);
        }

        String hash;
        do {
            hash = hashes.poll();
        }
        while (hash == null);

        return hash;
    }

    @Transactional
    public void fillHashes() {
        log.info("Fill hashes");
        isFilling.set(true);

        do {
            hashes.addAll(hashesRepository.getHashBatch(hashesQueueSize));
        }
        while (!ready.get());

        hashGenerator.generateBatch().thenAccept(result -> isFilling.set(false));
    }
}
