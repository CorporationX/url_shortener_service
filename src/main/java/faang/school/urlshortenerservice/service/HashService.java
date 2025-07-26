package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@Component
public class HashService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final Executor saveHashBatchExecutor;
    private final Executor fillingMemoryCacheExecutor;

    public HashService(HashRepository hashRepository,
                       Base62Encoder base62Encoder,
                       @Qualifier("fillingMemoryCacheExecutor") Executor fillingMemoryCacheExecutor,
                       @Qualifier("saveHashBatchExecutor") Executor saveHashBatchExecutor) {
        this.hashRepository = hashRepository;
        this.base62Encoder = base62Encoder;
        this.fillingMemoryCacheExecutor = fillingMemoryCacheExecutor;
        this.saveHashBatchExecutor = saveHashBatchExecutor;
    }

    @Value("${app.hash.table-size:10000}")
    private long tableSize;
    @Value("${app.hash.lock-id}")
    private int lockId;
    @Value("${app.hash.batch-size:500}")
    private int batchSize;

    @Value("${app.hash.table-min-percent:20.0}")
    private double tableMinPercent;

    public CompletableFuture<List<String>> getHashesAsync(long count) {
        return CompletableFuture.supplyAsync(() -> getHashes(count), fillingMemoryCacheExecutor);
    }

    @Transactional
    public List<String> getHashes(long count) {
        List<String> hashes = getHashList(count);

        if (hashes.size() < count) {
            List<String> newHashes = getHashesFromSequence(count - hashes.size())
                    .stream()
                    .map(Hash::getHash)
                    .toList();
            hashes.addAll(newHashes);
        }
        return hashes;
    }

    private List<String> getHashList(long count) {
        List<String> hashes = hashRepository.findAndDeleteLimit(count)
                .stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());

        generateHashBatchIfNeeded();

        return hashes;
    }

    private void generateHashBatchIfNeeded() {
        if (!hashRepository.tryLock(lockId)) {
            long currentHashCount = hashRepository.count();
            if (checkCurrentPercent(currentHashCount)) {
                getHashesFromSequenceAsync(tableSize - currentHashCount)
                        .thenAccept(hashes -> {
                            List<Hash> hashesSaved = hashRepository.saveAll(hashes);
                            log.info("Hashes {} has been saved", hashesSaved);
                        })
                        .whenComplete((result, throwable) -> {
                            hashRepository.unlock(lockId);
                            if (throwable != null) {
                                log.error("An error occurred during asynchronous hash generation", throwable);
                            }
                        });
            }
        }
    }

    private List<Hash> encodeBatch(List<Long> batchNumbers) {
        return batchNumbers
                .stream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }

    private boolean checkCurrentPercent(long currentHashCount) {
        return (currentHashCount * 100.0 / tableSize) <= tableMinPercent;
    }

    private CompletableFuture<List<Hash>> getHashesFromSequenceAsync(long missingCount) {
        return CompletableFuture.supplyAsync(() -> getHashesFromSequence(missingCount), saveHashBatchExecutor);
    }

    private List<Hash> getHashesFromSequence(long missingCount) {
        List<Integer> batches = splitIntoBatches(missingCount);

        List<Hash> hashes = batches.stream()
                .map(this::getHashesForBatchAsync)
                .map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Collections.shuffle(hashes);
        return hashes;
    }

    private List<Integer> splitIntoBatches(long count) {
        return LongStream.range(0, count)
                .boxed()
                .collect(Collectors.groupingBy(i -> i / batchSize))
                .values()
                .stream()
                .map(List::size)
                .toList();
    }

    private CompletableFuture<List<Hash>> getHashesForBatchAsync(int batchSize) {
        return CompletableFuture.supplyAsync(() -> {
            List<Long> numbers = hashRepository.getNextSequenceValues(batchSize);
            return encodeBatch(numbers);
        }, saveHashBatchExecutor);
    }
}
