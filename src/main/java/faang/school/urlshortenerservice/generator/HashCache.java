package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashJpaRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
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
@Data
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;
    private final HashJpaRepository hashRepository;

    @Value("${hash.cache.capacity:5}")
    private int capacity;

    @Value("${hash.cache.fill.percent:20}")
    private int fillPercent;

    private final AtomicBoolean filling = new AtomicBoolean(false);

    private Queue<String> cache;

    @PostConstruct
    private void init() {
        this.cache = new ArrayBlockingQueue<>(capacity);
        try {
            refreshCache();
            log.info("hashes all = {} ", cache.size());
        } catch (Exception e) {
            log.error("Error {}", e);
        }
    }

    public String getHash() {
        if ((cache.size() * (capacity / 100.0)) < fillPercent) {
            if (filling.compareAndSet(false, true)) {
                refreshCache();
                return cache.poll();
            }
        }
        return cache.poll();
    }

    public void refreshCache() {
        executorService.submit(() ->
        {
            int needSize = capacity - cache.size();
            hashGenerator.generateBatch();
            List<String> hashes = hashRepository.findAndDelete(needSize);
            cache.addAll(hashes);
            filling.set(false);
        });
    }
}