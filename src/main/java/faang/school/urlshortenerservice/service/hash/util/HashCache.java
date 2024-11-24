package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class HashCache {
    private static final int THREAD_POOL_SIZE = 2;

    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean cacheIsUpdating = new AtomicBoolean(false);

    @Value("${app.hash_cache.hashes_max_size}")
    private int hashesMax;

    @Value("${app.hash_cache.hashes_min_size}")
    private int hashesMin;

    public HashCache(HashService hashService, HashGenerator hashGenerator) {
        this.hashService = hashService;
        this.hashGenerator = hashGenerator;

        cacheIsUpdating.set(true);
        updateHashes();
    }

    public String getHash() {
        checkHashesSize();
        return hashes.poll();
    }

    private void checkHashesSize() {
        if (cacheIsUpdating.compareAndSet(false, true)) {
            if (hashes.size() < hashesMin) {
                executor.execute(this::updateHashesAndExecuteAsyncGenerateHashes);
            } else {
                cacheIsUpdating.set(false);
            }
        }
    }

    private void updateHashesAndExecuteAsyncGenerateHashes() {
        updateHashes();
        executor.execute(hashGenerator::generate);
    }

    private void updateHashes() {
        try {
            List<String> newHashes = hashService.findAllByPackSize(hashesMax);
            hashes.addAll(newHashes);
        } finally {
            cacheIsUpdating.set(false);
        }
    }
}
