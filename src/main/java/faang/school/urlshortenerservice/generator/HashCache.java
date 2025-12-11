package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;

    private final ConcurrentLinkedQueue<String> pool = new ConcurrentLinkedQueue<>();

    @Value("${hash.cache.capacity}")
    private int hashCapacity;

    @Value("${hash.cache.fill.ratio}")
    private float hashRefillRatio;

    @PostConstruct
    public void init() {
        refillCache();
    }

    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    @Transactional
    public String getHash() {
        String hash = pool.poll();
        if (hash == null) {
            long id = hashRepository.findNextUnusedId();
            hashRepository.markUsed(id);
            return encodeBase62(id);
        }
        if ((pool.size() < hashCapacity / hashRefillRatio) && (refillInProgress
                .compareAndSet(false,true))) {
            refillCache();
            refillInProgress.set(false);
        }
        return hash;
    }

    @Async
    public void refillCache() {
        List<Long> ids = hashRepository.getFreeIds(hashCapacity);
        ids.forEach(id -> pool.add(encodeBase62(id)));
    }

    private String encodeBase62(long num) {
        final String b62Chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(b62Chars.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}
