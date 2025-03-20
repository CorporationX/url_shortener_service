package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class HashCache {
    private final HashRepository hashRepository;
    private final HashGeneratorService hashGeneratorService;
    private final ExecutorService hashesExecutorService;

    private final AtomicBoolean hashesGenerationInProgress = new AtomicBoolean(false);

    private Queue<String> hashesCache;

    @Value("${hashes.cache-capacity:10}")
    private int cacheCapacity;

    @PostConstruct
    public void init() {
        hashesCache = new ArrayBlockingQueue<>(cacheCapacity);
        fillCache();
    }

    public String getCachedHash() {
        if (!hashesGenerationInProgress.get() && isHashSupplyLow()) {
            fillCacheAsync();
        }
        return hashesCache.poll();
    }

    public List<String> getHashes(int amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            int remainingAmount = amount - hashes.size();
            hashGeneratorService.generateBatch(remainingAmount);
            hashes.addAll(hashRepository.findAndDelete(remainingAmount));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    private void fillCache() {
        hashesGenerationInProgress.set(true);
        List<String> hashesStrings = getHashes(cacheCapacity);
        hashesCache.addAll(hashesStrings);
        hashesGenerationInProgress.set(false);
    }

    private void fillCacheAsync() {
        CompletableFuture.runAsync(this::fillCache, hashesExecutorService);
    }

    private boolean isHashSupplyLow() {
        return (float) hashesCache.size() / cacheCapacity <= 0.2f;
    }
}
