package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    @Value("${hash.cache.maxCapacity:1000}")
    Integer maxCapacity;
    @Value("${hash.cache.border:0.2}")
    Double border;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean fillingNow = new AtomicBoolean(false);
    private Queue<String> hashQueue;

    @PostConstruct
    private void init() {
        hashQueue = new ArrayBlockingQueue<>(maxCapacity);
        hashQueue.addAll(hashGenerator.getHashes(maxCapacity));
    }

    @Retryable
    public String getHash() {
        if (hashQueue.size() < border * maxCapacity) {
            if (fillingNow.compareAndExchange(false, true)) {
                hashGenerator.getHashesAsync(maxCapacity)
                        .thenAccept(hashQueue::addAll)
                        .thenRun(() -> fillingNow.set(false));
            }
        }
        return hashQueue.poll();
    }
}
