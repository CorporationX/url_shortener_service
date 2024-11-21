package faang.school.urlshortenerservice.service.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCacheImpl implements HashCache {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
    private final ExecutorService executorService;
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();
    private final AtomicBoolean isReplenishing = new AtomicBoolean(false);
    private int cacheRefillThreshold;

    @Value("${server.hash.fetch.batch.size}")
    private int fetchHashesSize;
    @Value("${server.hash.fetch.batch.min-size-percentage}")
    private int hashesMinSizeAsPercentage;

    @PostConstruct
    public void initFreeHashes() {
        log.info("Initializing hash cache...");
        hashGenerator.generateBatch();
        cacheRefillThreshold = (int) (fetchHashesSize * hashesMinSizeAsPercentage / 100.0);
        log.info("Cache refill threshold set to {}", cacheRefillThreshold);
        fetchFreeHashes();
    }

    @Override
    public String getHash() {
        if (hashes.size() <= cacheRefillThreshold && isReplenishing.compareAndSet(false, true)) {
            fetchFreeHashes();
        }
        return Optional.ofNullable(hashes.poll())
                .orElseThrow(() -> {
                    log.error("Failed to retrieve hash: Hash cache is empty!");
                    return new RuntimeException("Free hash not found!");
                });
    }

    private void fetchFreeHashes() {
        log.info("Fetching {} new hashes from repository...", fetchHashesSize);
        executorService.execute(() -> {
            try {
                List<String> newFreeHashes = hashRepository.getHashBatch(fetchHashesSize);
                log.info("Fetched {} hashes from repository.", newFreeHashes.size());
                hashes.addAll(newFreeHashes);
                hashGenerator.generateBatch();
                log.info("Generated new hash batch.");
            } catch (Exception e) {
                log.error("Error while fetching or generating hashes", e);
            } finally {
                isReplenishing.set(false);
            }
        });
    }
}