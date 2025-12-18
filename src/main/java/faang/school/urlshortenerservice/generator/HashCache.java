package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor asyncHashGenerationExecutor;
    private final HashEncoder hashEncoder;
    private final ConcurrentLinkedDeque<String> hashPool = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @Value("${hash.cache.capacity}")
    private int hashCapacity;

    @Value("${hash.cache.fill.ratio}")
    private float hashRefillRatio;

    @PostConstruct
    public void init() {
        log.info("Performing startup hash pool refill");
        asyncHashGenerationExecutor.execute(this::refillCache);
    }
    @Transactional
    public String getHash() {
        log.info("Checking hash pool");
        String hash = hashPool.poll();
        if (hash == null) {
            log.info("Hash pool is empty, generating one now");
            long id = hashRepository.findNextUnusedId();
            hashRepository.markUsed(id);
            hash = hashEncoder.encodeBase62(id);
        }
        if ((hashPool.size() < (hashCapacity * hashRefillRatio)) && (refillInProgress
                .compareAndSet(false, true))) {
            log.info("Available hash pool size is lower than 20%");
            asyncHashGenerationExecutor.execute(this::refillCache);
        }
        return hash;
    }

    @Transactional
    protected void refillCache() {
        log.info("Refilling hash pool");
        try {
            List<Long> ids = hashRepository.getFreeIds(hashCapacity);
            ids.forEach(id -> hashPool.add(hashEncoder.encodeBase62(id)));
        } finally {
            refillInProgress.set(false);
            log.info("Hash pool refilled");
        }
    }
}