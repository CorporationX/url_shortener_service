package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ExecutorService executorService;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);

    @Value("${cache.size:1000}")
    private int cacheSize;

    @Value("${cache.fill_percent:20.0}")
    private double fillPercent;

    private Queue<String> cache;

    @PostConstruct
    public void init() {
        cache = new ArrayBlockingQueue<>(cacheSize);
        fillCache();
    }

    public String getHash() {
        if (cacheSize / 100.0 * fillPercent > cache.size() && isFilling.compareAndSet(false, true)) {
            fillCache();
        }
        return cache.poll();
    }

    private void fillCache() {
        executorService.submit(() -> {
            try {
                List<String> newHashes = hashRepository.getHashBatch();
                hashGenerator.generateBatch();
                cache.addAll(newHashes);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                isFilling.set(false);
            }
        });
    }
}
