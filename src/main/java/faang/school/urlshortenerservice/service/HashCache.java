package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        executorService.submit(this::refillCache);
    }

    @Value("${hash.cache.size}")
    private int cacheSize;

    @Value("${hash.cache.refill_percent}")
    private int refillPercent;

    private final BlockingDeque<String> availableHashes = new LinkedBlockingDeque<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);


    public Optional<String> getHash() {
        String hash = availableHashes.pollFirst();

        if (shouldRefill()) {
            scheduleRefill();
        }
        return Optional.ofNullable(hash);
    }

    private boolean shouldRefill() {
        return availableHashes.size() < (cacheSize * refillPercent / 100);
    }

    private void scheduleRefill() {
        if (refillInProgress.compareAndSet(false, true)) {
            executorService.submit(this::refillCache);
        }
    }

    private void refillCache() {
        hashGenerator.generateBatch();

        List<String> newHashes = hashRepository.getHashBatch(cacheSize);
        availableHashes.addAll(newHashes);
    }
}
