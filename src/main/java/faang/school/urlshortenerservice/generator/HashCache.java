package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;

    private final ConcurrentLinkedQueue<String> hashPool = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @Value("${hash.cache.capacity}")
    private int hashCapacity;

    @Value("${hash.cache.fill.ratio}")
    private float hashRefillRatio;

    @PostConstruct
    public void init() {
        log.info("Performing startup hash pool refill");
        refillCache();
    }

    @Transactional
    public String getHash() {
        log.info("Checking hash pool");
        String hash = hashPool.poll();
        if (hash == null) {
            log.info("Hash pool is empty, generating one now");
            long id = hashRepository.findNextUnusedId();
            hashRepository.markUsed(id);
            hash = encodeBase62(id);
        }
        if ((hashPool.size() < (hashCapacity * hashRefillRatio)) && (refillInProgress
                .compareAndSet(false, true))) {
            log.info("Available hash pool size is lower than 20%");
            refillCache();
            refillInProgress.set(false);
            log.info("Hash pool refilled");
        }
        return hash;
    }

    @Async("asyncHashGenerationExecutor")
    public void refillCache() {
        log.info("{} is refilling hash pool.",Thread.currentThread().getName());
        List<Long> ids = hashRepository.getFreeIds(hashCapacity);
        ids.forEach(id -> hashPool.add(encodeBase62(id)));
    }

    private String encodeBase62(long num) {
        final String b62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        log.info("Generating hash value");
        while (num > 0) {
            sb.append(b62Chars.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}
