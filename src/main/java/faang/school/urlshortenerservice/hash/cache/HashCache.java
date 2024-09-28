package faang.school.urlshortenerservice.hash.cache;

import faang.school.urlshortenerservice.exception.UniqueHashNotFoundException;
import faang.school.urlshortenerservice.hash.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final AtomicBoolean generateHashesRunning = new AtomicBoolean(false);
    @Value("${local-cache.minimalAmountInPercentage}")
    private int minimalAmountInPercentage;
    @Value("${local-cache.maxSize}")
    private int maxSize;
    private int minimalAmount;

    @PostConstruct
    public void init() {
        minimalAmount = maxSize * minimalAmountInPercentage / 100;
        addAndGenerateHashesIfNecessary();
    }

    @Retryable(
            retryFor = UniqueHashNotFoundException.class,
            maxAttemptsExpression = "${retryable.hash-cash.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${retryable.hash-cash.delay}",
                    multiplierExpression = "${retryable.hash-cash.multiplier}",
                    maxDelayExpression = "${retryable.hash-cash.maxDelay}"))
    public String getNextUniqueHash() {
        addAndGenerateHashesIfNecessary();
        String nextHash = cache.poll();
        if (nextHash == null) {
            throw new UniqueHashNotFoundException();
        }
        return nextHash;
    }

    private void addAndGenerateHashesIfNecessary() {
        if (generateHashesRunning.compareAndSet(false, true)) {
            taskExecutor.submit(() -> {
                if (cache.isEmpty() || cache.size() <= minimalAmount) {
                    cache.addAll(hashRepository.getHashBatch());
                    hashGenerator.generateHash();
                    generateHashesRunning.set(false);
                }
            });
        }
    }

}
