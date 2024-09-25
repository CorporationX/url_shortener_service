package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator generator;

    @Value("${hash.min_percentage_free_hash}")
    private double minPercentage;
    @Value("${hash.count_hash}")
    private int maxQueueSize;
    private LinkedBlockingQueue<String> queueHash;
    private final AtomicBoolean lock = new AtomicBoolean(false);

    @PostConstruct
    private void init() {
        this.queueHash = new LinkedBlockingQueue<>(maxQueueSize);
    }

    @Async("hashGeneratorExecutor")
    public synchronized void generateHashes() {
        if (lock.compareAndSet(false, true)) {
            generator.generateBatch();
            hashRepository.getHashBatch(maxQueueSize - queueHash.size())
                    .forEach(hash -> queueHash.offer(hash));
            lock.set(false);
        }
    }

    public String getHashFromUser() {
        if (maxQueueSize * minPercentage >= queueHash.size()) {
            generateHashes();
        }
        return queueHash.poll();
    }

}
