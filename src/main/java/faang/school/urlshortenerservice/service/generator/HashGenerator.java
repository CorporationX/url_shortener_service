package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.db_cache.batch_size}")
    private int minCacheSizeAtDB;

    @Value("${hash.db_cache.min_load_factor}")
    private int minLoadFactor;

    public List<String> generateHashes(long amount) {
        List<Long> range = hashRepository.getUniqueNumbers(amount);
        return base62Encoder.encode(range);
    }

    @Async("hashGeneratorExecutor")
    public void generateHashesAsync() {
        long count = hashRepository.count();
        if (!isEnoughHashes(count)) {
            List<String> hashes = generateHashes(minCacheSizeAtDB - count);
            List<Hash> batch = hashes.stream()
                    .map(Hash::new)
                    .toList();
            hashRepository.saveAll(batch);
            log.info("{} hashes generated", minCacheSizeAtDB - count);
        }
    }

    @Transactional
    @Async("getHashExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public List<String> getHashes(long amount) {
        return hashRepository.findAndDelete(amount);
    }

    private boolean isEnoughHashes(long hashSize) {
        int loadFactor = (int) ((hashSize * 100) / minCacheSizeAtDB);
        return loadFactor > minLoadFactor;
    }
}
