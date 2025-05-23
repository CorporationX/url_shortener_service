package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.properties.CacheProperties;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final JdbcHashRepository jdbcHashRepository;
    private final HashService hashService;
    private final CacheProperties cacheProperties;

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean refillInProgress = new AtomicBoolean(false);

    public String poll() {
        if (cache.size() < cacheProperties.getMinSize()
                && refillInProgress.compareAndSet(false, true)) {
            hashService.generateMoreHashes();
        }

        String hash = cache.poll();
        if (hash == null) {
            List<String> fallback = jdbcHashRepository.getAndRemoveBatch(cacheProperties.getFillSize());
            if (fallback.isEmpty()) {
                throw new IllegalStateException("No hashes available");
            }
            addAll(fallback);
            hash = cache.poll();
        }

        return hash;
    }

    public void addAll(List<String> hashes) {
        cache.addAll(hashes);
        refillInProgress.set(false);
        log.info("Hash cache replenished with {} hashes", hashes.size());
    }

    public int size() {
        return cache.size();
    }
}