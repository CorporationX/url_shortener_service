package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashException;
import faang.school.urlshortenerservice.hashGenerator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {

    @Value("${hash.hash_batch_and_delete_size}")
    private int hashBatchSize;

    @Value("${hash.cache_size}")
    private int cacheSize;

    @Value("${hash.percent_to_refresh_cache}")
    private int percentToRefreshCache;

    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    private final BlockingQueue<String> hashQueue = new LinkedBlockingDeque<>();
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        log.info("Initializing hash cache");
        hashGenerator.generateHash().thenAccept(hashes -> hashQueue.addAll(hashes.stream().map(Hash::getHash).toList()));
        hashQueue.addAll(hashRepository.getHashBatchAndDelete(hashBatchSize));
    }

    public String getHash() {
        if (isCacheFullness()) {
            log.info("Hash cache is less than {}% ,generating new hash", percentToRefreshCache);
            if (isGenerating.compareAndExchange(false, true)) {
                hashGenerator.generateHash()
                        .thenAccept(hashes -> {
                            List<String> list = hashes.stream().map(Hash::getHash).toList();
                            hashQueue.addAll(list);
                        })
                        .thenRun(() -> isGenerating.set(false));
                log.info("Hash generated and added to cache");
            }
        }
        try {
            return hashQueue.take();
        } catch (InterruptedException e) {
            log.error("Failed to get hash from cache", e);
            throw new HashException(e.getMessage());
        }
    }

    private boolean isCacheFullness() {
        return hashQueue.size() / (cacheSize / 100) > percentToRefreshCache;
    }
}
