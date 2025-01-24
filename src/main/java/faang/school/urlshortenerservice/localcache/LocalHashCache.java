package faang.school.urlshortenerservice.localcache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
@RequiredArgsConstructor
@Slf4j
public class LocalHashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Value("${hash.local_cache.size}")
    private int localCacheCapacity;
    @Value("${hash.local_cache.min_size_threshold}")
    private double minSizeThreshold;
    @Value("${hash.local_cache.waiting_local_cache_update_timeout}")
    private long waitingLocalCacheUpdateTimeout;
    private final AtomicBoolean cacheUpdatingFlag = new AtomicBoolean(false);
    private ArrayBlockingQueue<Hash> hashQueue;

    @PostConstruct
    public void init() {
        hashQueue = new ArrayBlockingQueue<>(localCacheCapacity);
        fillUpLocalCache();
    }

    public String getCachedHash() {
        if (hashQueue.size() <= minSizeThreshold * localCacheCapacity) {
            log.info("local cache almost empty with size {}", hashQueue.size());
            fillUpLocalCache();
        }
        Hash hash;
        try {
            hash = hashQueue.poll(waitingLocalCacheUpdateTimeout, MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (hash == null) {
            throw new RuntimeException("Couldn't get hash from local cache, timeout of %s milliseconds".formatted(waitingLocalCacheUpdateTimeout));
        }
        return hash.getHash();
    }

    private void fillUpLocalCache() {
        if (cacheUpdatingFlag.compareAndSet(false, true)) {
            hashGenerator.generateBatch().thenRun(() -> {
                log.info("start filling up local hash cache, getting hashes from DB");
                List<Hash> hashes = hashRepository.getHashBatch(hashQueue.remainingCapacity());

                log.info("add hashes to local cache {}", hashes);
                hashQueue.addAll(hashes);

                cacheUpdatingFlag.set(false);
                log.info("local cache has been updated");
            });
        }
    }
}
