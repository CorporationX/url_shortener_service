package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash-cache.queue.size}")
    private int queueSize;

    @Value("${hash-cache.queue.threshold}")
    private int fillThreshold;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    private final Queue<Hash> hashQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    public Hash getHash() {
        if (hashQueue.size() <= getMinQueueSize() && isFilling.compareAndSet(false, true)) {
            executorService.submit(() -> {
                List<Hash> newHashes = hashRepository.findAll();
                hashGenerator.generateHashes();
                hashQueue.addAll(newHashes);
                isFilling.set(false);
            });
        }
        return hashQueue.poll();
    }

    private int getMinQueueSize() {
        return fillThreshold * queueSize / 100;
    }
}
