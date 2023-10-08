package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash-cache.queue.size:1000}")
    private int queueSize;

    @Value("${hash-cache.queue.threshold:20}")
    private int fillThreshold;

    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    private final Queue<Hash> hashQueue = new ConcurrentLinkedQueue<>();
    private final ExecutorService executorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void init() {
        if (hashQueue.size() == 0) {
            addHashes();
        }
    }

    public Hash getHash() {
        if (hashQueue.size() <= getMinQueueSize() && isFilling.compareAndSet(false, true)) {
            addHashes();
        }

        return hashQueue.poll();
    }

    private void addHashes() {
        executorService.submit(() -> {
            try {
                List<Hash> newHashes = hashRepository.findAll();
                hashGenerator.generateBatch();
                hashQueue.addAll(newHashes);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                isFilling.set(false);
            }
        });
    }

    private int getMinQueueSize() {
        return fillThreshold * queueSize / 100;
    }
}
