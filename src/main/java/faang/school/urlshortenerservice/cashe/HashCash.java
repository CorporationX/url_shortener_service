package faang.school.urlshortenerservice.cashe;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.hash_generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class HashCash {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    private final AtomicBoolean isGenerating = new AtomicBoolean(false);
    @Value("${hash.queue_capacity}")
    private int capacity;
    @Value("${hash.min_fill_percent}")
    private int minFillPercent;
    private  BlockingQueue<Hash> queue;

    @PostConstruct
    public void init() {
        log.info("Initializing Hash Cash");
        queue = new LinkedBlockingDeque<>(capacity);
        fillingQueue();
    }

    @Async("executorService")
    public CompletableFuture<Hash> getHash(int size) {
        if (queue.size() / (capacity / 100.0) < minFillPercent) {
            if (isGenerating.compareAndSet(false, true)) {
            }
            fillingQueue();
            isGenerating.set(false);
            log.info("Generated new batch hashes");
        }
        return CompletableFuture.completedFuture(queue.poll());
    }

    private void fillingQueue() {
        hashGenerator.generatedBatch();
        queue.addAll(hashRepository.getHashBatch(capacity));
    }
}
