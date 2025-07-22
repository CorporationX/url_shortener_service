package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

    @Transactional
    public List<String> getHashes(long count) {
        List<String> hashes = getHashList(count);

        if (hashes.size() < count) {
            List<String> newHashes = getHashesFromSequence(count - hashes.size());
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
        return CompletableFuture.completedFuture(getHashesFromSequence(count));
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

            List<Hash> hashes = getHashesFromSequence(missingCount)
                    .stream()
                    .map(Hash::new)
                    .toList();

            saveAll(hashes);
            log.info("Hashes {} has been saved", hashes);
        } finally {
            hashRepository.unlock(lockId);
        }
    }

    @Transactional
    public List<Hash> saveAll(List<Hash> hashes) {
        List<Hash> hashesSaved = hashRepository.saveAll(hashes);
        log.info("Hashes {} has been saved", hashesSaved);

        return hashesSaved;
    }


    private List<String> encodeBatch(List<Long> batchNumbers) {
        return batchNumbers
                .stream()
                .map(base62Encoder::encode)
                .toList();
    }

    // TODO: можно улучить
    private List<String> getHashesFromSequence(long missingCount) {
        List <Long> numbers = hashRepository.getNextSequenceValues(missingCount);

        List<List<Long>> batchNumbers = ListUtils.partition(numbers, batchSize);

        List<CompletableFuture<List<String>>> futureBatchHash = batchNumbers.stream()
                .map(batch -> CompletableFuture.supplyAsync(
                        () -> encodeBatch(batch), saveHashBatchExecutor)
                )
                .toList();

        return futureBatchHash.stream()
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .toList();
    }
}
