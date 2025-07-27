package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.hash.HashGenerator;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashCacheServiceImpl implements HashCacheService{
    @Value("${hash.batch-size:10}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    private final AtomicBoolean isHashDBGenerating = new AtomicBoolean(false);

    @Override
    @Transactional
    @Async("hashGenExecutor")
    public CompletableFuture<List<Hash>> getHashBatchAsync() {
        List<Hash> hashes = hashRepository.getHashBatch(batchSize);

        if (hashes.size() < batchSize && isHashDBGenerating.compareAndSet(false, true)) {
            hashGenerator.generateHashesAsync().whenComplete((v, exception) -> {
                if (exception != null) {
                    log.error("Error during async hash generation", exception);
                }
                isHashDBGenerating.set(false);
            });
        }

        return CompletableFuture.completedFuture(hashes);
    }

    @Override
    @Transactional
    public List<Hash> getStartHashes() {
        hashGenerator.generateHashes();
        return hashRepository.getHashBatch(batchSize);
    }
}
