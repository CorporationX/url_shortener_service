package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

@Component
public class LocalHashCache {
    private final Integer capacity;
    private final Double threshold;
    private final Queue<String> cache;
    private final Executor hashGeneratorExecutor;
    private final HashService hashService;
    private final HashGenerator hashGenerator;

    public LocalHashCache(HashService hashService,
                          HashGenerator hashGenerator,
                          @Value("${app.cache.capacity}") Integer capacity,
                          @Value("${app.cache.threshold}") Double threshold,
                          Executor hashGeneratorExecutor) {
        this.hashService = hashService;
        this.hashGenerator = hashGenerator;
        this.capacity = capacity;
        this.threshold = threshold;
        this.cache = new ArrayBlockingQueue<>(capacity);
        this.hashGeneratorExecutor = hashGeneratorExecutor;
    }

    @PostConstruct
    public void fillCache() {
        updateLocalCache();
    }

    public String getHash() {
        String hash = cache.poll();
        if (cache.size() <= capacity * threshold) {
            hashGeneratorExecutor.execute(this::updateLocalCache);
        }
        return hash;
    }

    @Transactional
    public void updateLocalCache() {
        var hashes = hashService.getAndDeleteHashBatch();
        cache.addAll(hashes);
        hashGenerator.generateBatch();
    }
}
