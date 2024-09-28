package faang.school.urlshortenerservice.caсhe;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashCacheException;
import faang.school.urlshortenerservice.hash_generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    @Value("${hash.queue_capacity:1000}")
    private int capacity;
    @Value("${hash.min_fill_percent:20}")
    private int minFillPercent;
    private BlockingQueue<Hash> queue;

    @PostConstruct
    public void init() {
        log.info("Initializing Hash Cash");
        queue = new LinkedBlockingDeque<>(capacity);
        fillingQueue();
    }

    public String getHash() {
        if (isGenerating.compareAndSet(false, true)) {
            if (queue.remainingCapacity() / (capacity / 100.0) < minFillPercent) {
                hashGenerator.getBatch();
                hashGenerator.getBatchAsync().thenAccept(queue::addAll);
                isGenerating.set(false);
                log.info("Generated new batch hashes");
            }
        }
        try {
            return queue.poll(1000l, TimeUnit.MILLISECONDS).getHash();
        } catch (InterruptedException e) {
            String msg = "Queue is empty";
            log.error(msg, e);
            throw new HashCacheException(msg);
        }
    }

    public void fillingQueue() {
        hashGenerator.generatedBatch();
        hashGenerator.getBatch().stream().forEach(queue::offer);
    }
}
