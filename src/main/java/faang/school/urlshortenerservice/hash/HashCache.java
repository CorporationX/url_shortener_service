package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.properties.CacheProperties;
import faang.school.urlshortenerservice.service.AsyncHashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashGenerator hashGenerator;
    private final CacheProperties cacheProps;
    private final AsyncHashService asyncHashService;
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final Queue<String> hashes = new ConcurrentLinkedDeque<>();

    @PostConstruct
    private void init() {
        hashes.addAll(hashGenerator.getHashes());
    }

    public String getHash() {
        checkAndFillCache();
        return pollHash();
    }

    private void checkAndFillCache() {
        if (shouldFillCache() && isFilling.compareAndSet(false, true)) {
            asyncHashService.fillHashCacheAsync();
        }
    }

    private boolean shouldFillCache() {
        int currentSize = hashes.size();
        if (hashes.size() >= cacheProps.getCapacity()) {
            return false;
        }
        return currentSize * cacheProps.getMaxPercentage() / cacheProps.getCapacity() < cacheProps.getFillPercentage();
    }

    private String pollHash() {
        String hash = hashes.poll();
        if (hash == null) {
            throw new NoSuchElementException("There are no available hashes");
        }
        return hash;
    }
}
