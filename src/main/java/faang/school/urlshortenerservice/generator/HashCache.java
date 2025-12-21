package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.exception.NoFreeHashesException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final Queue<String> cache = new ConcurrentLinkedQueue<>();

    @Value("${hash.cache.min.size:20}")
    private int minCacheSize;

    public String getHash() {
        String hash = cache.poll();

        if (hash == null) {
            refillCacheIfNeeded();
            hash = cache.poll();
        }

        if (hash == null) {
            throw new NoFreeHashesException("No free hashes available");
        }

        refillCacheIfNeeded();

        return hash;
    }

    private void refillCacheIfNeeded() {
        if (cache.size() >= minCacheSize) {
            return;
        }

        int need = minCacheSize - cache.size();

        List<String> hashes = hashRepository.getHashBatch(need);

        cache.addAll(hashes);

        if (hashes.size() < need) {
            hashGenerator.generateBatch();
        }
    }
}