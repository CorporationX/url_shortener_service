package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final ThreadPoolTaskExecutor threadPool;
    private final HashProperties hashProperties;
    private final Queue<String> hashCache;
    private final AtomicBoolean isUpdating;

    public HashCache(HashGenerator hashGenerator,
                     HashRepository hashRepository,
                     ThreadPoolTaskExecutor threadPool,
                     HashProperties hashProperties) {
        this.hashGenerator = hashGenerator;
        this.hashRepository = hashRepository;
        this.threadPool = threadPool;
        this.hashProperties = hashProperties;
        this.hashCache = new ArrayBlockingQueue<>(hashProperties.getCacheCapacity(), true);
        this.isUpdating = new AtomicBoolean(false);

        initializeCache();
    }

    public String getHash() {
        String hash = hashCache.poll();
        updateCacheIfNeeded();
        if (Objects.isNull(hash)) {
            throw new IllegalStateException("Cache is empty");
        }
        return hash;
    }

    private void updateCacheIfNeeded() {
        if (isCacheLow() && isUpdating.compareAndSet(false, true)) {
            CompletableFuture.runAsync(this::getNewHashes, threadPool)
                    .thenRun(hashGenerator::generate)
                    .thenRun(() -> isUpdating.set(false));
        }
    }

    private boolean isCacheLow() {
        int lowThreshold = (int) (hashProperties.getCacheCapacity() * hashProperties.getLowThreshold());
        return hashCache.size() < lowThreshold;
    }

    private void getNewHashes() {
        List<String> hashBatch = hashRepository.getHashBatch();
        hashCache.addAll(hashBatch);
    }

    private void initializeCache() {
        hashGenerator.generate();
        List<String> initialBatch = hashRepository.getHashBatch();
        hashCache.addAll(initialBatch);
    }
}

