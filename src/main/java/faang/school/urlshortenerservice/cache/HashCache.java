package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.properties.HashCacheProperties;
import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final HashGenerator hashGenerator;
    private final ThreadPoolTaskExecutor hashCacheTaskExecutor;

    private LinkedBlockingQueue<String> cachedHashes;
    private final AtomicBoolean isFetching = new AtomicBoolean(false);
    private final HashCacheProperties hashCacheProperties;

    @PostConstruct
    public void initCachedHashes() {
        cachedHashes = new LinkedBlockingQueue<>(hashCacheProperties.getCapacity());
        cachedHashes.addAll(hashGenerator.getHashes(hashCacheProperties.getCapacity()));
    }

    @PreDestroy
    public void saveBackUnusedHashes() {
        hashGenerator.saveHashes(new ArrayList<>(cachedHashes));
    }

    public String getHash() {
        if (isCacheSizeLessMinimumRequiredSize() && isFetching.compareAndSet(false, true)) {
            int countHashesToFetch = hashCacheProperties.getCapacity() - cachedHashes.size();
            CompletableFuture
                    .supplyAsync(() -> hashGenerator.getHashes(countHashesToFetch), hashCacheTaskExecutor)
                    .thenAccept(cachedHashes::addAll)
                    .thenRun(() -> {
                        if (cachedHashes.isEmpty()) {
                            CompletableFuture.supplyAsync(() -> hashGenerator.getHashes(countHashesToFetch), hashCacheTaskExecutor)
                                    .thenAccept(cachedHashes::addAll)
                                    .exceptionally((throwable) -> {
                                        log.error("Error during re-fetching hashes, error message: {}", throwable.getMessage());
                                        return null;
                                    });
                        }

                        isFetching.set(false);
                    })
                    .exceptionally((throwable) -> {
                        log.error("Error during getting hashes, error message: {}", throwable.getMessage());
                        isFetching.set(false);
                        return null;
                    });
        }
        return cachedHashes.poll();
    }

    private boolean isCacheSizeLessMinimumRequiredSize() {
        return (100.0 * cachedHashes.size())/(hashCacheProperties.getCapacity()) < hashCacheProperties.getMinimumRequiredSize();
    }
}
