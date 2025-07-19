package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
public class HashService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final Executor saveHashBatchExecutor;

    public HashService(HashRepository hashRepository,
                       Base62Encoder base62Encoder,
                       @Qualifier("saveHashBatchExecutor") Executor saveHashBatchExecutor) {
        this.hashRepository = hashRepository;
        this.base62Encoder = base62Encoder;
        this.saveHashBatchExecutor = saveHashBatchExecutor;
    }

    @Value("${app.hash.table-size:10000}")
    private long tableSize;
    @Value("${app.hash.lock-id}")
    private int lockId;
    @Value("${app.hash.batch-size}")
    private int batchSize;

    public List<String> getHashes(long count) {
        List<String> hashes = getHashList(count);

        if (hashes.size() < count) {
            List<String> newHashes = hashRepository.getNextSequenceValues(count - hashes.size())
                    .stream()
                    .map(base62Encoder::encode)
                    .toList();
            hashes.addAll(newHashes);
        }
        return hashes;
    }

    private List<String> getHashList(long count) {
        return hashRepository.findAndDeleteLimit(count)
                .stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());
    }

    @Async("fillingMemoryCacheExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long count) {
        return CompletableFuture.completedFuture(getHashes(count));
    }

    @Transactional
    public void generateHashBatchIfNeeded() {
        boolean lockAcquired = hashRepository.tryLock(lockId);
        if (!lockAcquired) {
            return;
        }

        try {
            long currentCount = hashRepository.count();
            long missingCount = tableSize - currentCount;
            if (missingCount <= 0) {
                return;
            }


            List<Long> numbers = hashRepository.getNextSequenceValues(missingCount);

            List<List<Long>> batchNumbers = ListUtils.partition(numbers, batchSize);

            List<CompletableFuture<List<Hash>>> futureBatchHash = batchNumbers.stream()
                    .map(batch -> CompletableFuture.supplyAsync(
                            () -> mapBatch(batch), saveHashBatchExecutor)
                    )
                    .toList();

            List<Hash> hashes = futureBatchHash.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Collection::stream)
                    .toList();

            hashRepository.saveAll(hashes);
        } finally {
            hashRepository.unlock(lockId);
        }
    }

    private List<Hash> mapBatch(List<Long> batchNumbers) {
        return batchNumbers
                .stream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }
}
