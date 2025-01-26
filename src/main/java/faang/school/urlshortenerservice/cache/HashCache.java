package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashCache {
    @Value("${hash.cache.queue.capacity:10000}")
    private int capacity;

    @Value("${hash.cache.threshold-percent:20}")
    private double thresholdPercent;

    private final ExecutorService hashExecutorService;
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final AtomicBoolean isRefreshing = new AtomicBoolean(false);
    private Queue<String> hashes;

    private int thresholdCache;

    @PostConstruct
    public void init() {
        hashes = new ArrayBlockingQueue<>(capacity);
        thresholdCache = (int) (capacity * thresholdPercent / 100);

        List<String> generatedHashes = hashGenerator.getHashList(capacity);
        try {
            hashes.addAll(generatedHashes);
        } catch (IllegalStateException e) {
            log.error("Error initializing HashCache: Queue is full", e);
        }
    }

    public String getHash() {
        if (checkRefresh()) {
            refreshCache();
        }
        return hashes.poll();
    }

    private void refreshCache() {
        if (isRefreshing.compareAndSet(false, true)) {
            hashExecutorService.submit(() -> {
                try {
                    List<String> hashList = hashRepository.getHashesAndDelete(capacity - hashes.size());
                    hashes.addAll(hashList);
                    hashGenerator.generateHashList();
                } catch (Exception e) {
                    log.error("Error during cache refresh", e);
                } finally {
                    isRefreshing.set(false);
                }
            });
        }
    }

    private boolean checkRefresh() {
        return hashes.size() <= thresholdCache;
    }
}
