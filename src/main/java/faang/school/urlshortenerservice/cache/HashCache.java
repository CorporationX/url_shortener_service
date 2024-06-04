package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashCache {

    private final HashGenerator hashGenerator;

    @Value("${app.hash.cache.size}")
    private int cacheSize;
    @Value("${app.hash.cache.fill-percent}")
    private int fillPercent;

    private AtomicBoolean isRefilling;
    private ArrayBlockingQueue<Hash> hashes;

    @PostConstruct
    public void init() {
        isRefilling = new AtomicBoolean(false);

        log.info("Starting init HashCache");
        hashes = new ArrayBlockingQueue<>(cacheSize);
        hashGenerator.getHashesAsync(cacheSize).thenAccept(hashes::addAll);
        log.info("Finished starting init HashCache, cache size: {}", cacheSize);
    }

    public Hash getHash() {
        if ((hashes.size() / (cacheSize / 100) < fillPercent)
                && (isRefilling.compareAndSet(false, true))) {
            log.info("Replenish the cache of hashes: {}", cacheSize);
            hashGenerator.getHashesAsync(cacheSize)
                    .thenAccept(hashes::addAll)
                    .thenRun(() -> isRefilling.set(false));
        }
        log.info("Finished replenish the cache of hashes: {}", cacheSize);
        return hashes.poll();
    }

}