package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    @Qualifier("hashCacheExecutor")
    private final Executor hashCacheExecutor;

    @Qualifier("hashGeneratorExecutor")
    private final Executor hashGeneratorExecutor;

    @Value(value = "${app.hash.table-size}")
    private long tableSize;

    @Value(value = "${app.hash.table-min-percentage}")
    private double minimumFillPercentage;

    @Value(value = "${app.hash.batch-size}")
    private int batchSize;

    @Value(value = "${app.hash.lock-id}")
    private int lockId;


    @Transactional
    public List<String> getHashes(long count) {
        List<String> hashes = getHashList(count);

        if (hashes.size() < count) {
            List<String> newHashes = getAndShuffleHashes(count)
                    .stream()
                    .map(Hash::getHash)
                    .toList();

            hashes.addAll(newHashes);
        }
        return hashes;
    }

    private List<String> getHashList(long count) {
        List<String> hashList = hashRepository.findAndDeleteLimit(count)
                .stream()
                .map(Hash::getHash)
                .collect(Collectors.toList());

        generateHashBatches();

        return hashList;
    }

    private void generateHashBatches() {
        if (hashRepository.tryLock(lockId)) {
            long currentHashCount = hashRepository.count();
            if (checkCurrentFillPercentage(currentHashCount)) {
                getAndShuffleHashesAsync(tableSize - currentHashCount)
                        .thenAccept(hashes -> {
                            List<Hash> savedHashes = hashRepository.saveAll(hashes);
                        })
                        .whenComplete((result, exception) -> {
                            hashRepository.unlock(lockId);
                            if (exception != null) {
                                log.error("Error occurred during cache generation", exception);
                            }
                        });
            }
        }
    }

    private CompletableFuture<List<Hash>> getAndShuffleHashesAsync(long count) {
        return CompletableFuture.supplyAsync(() -> getAndShuffleHashes(count), hashCacheExecutor);
    }

    private List<Hash> getAndShuffleHashes(long count) {
        List<Integer> batches = splitIntoBatches(count);

        List<Hash> hashes = batches.stream()
                .map(this::generateHashBatchAsync)
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

    private List<Hash> encodeBatch(List<Long> batchList) {
        return batchList
                .stream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }

    private CompletableFuture<List<Hash>> generateHashBatchAsync(long batchSize) {
        return CompletableFuture.supplyAsync(() -> {
            List<Long> batchList = hashRepository.getUniqueSequenceValues(batchSize);
            return encodeBatch(batchList);
        }, hashGeneratorExecutor);
    }

    private boolean checkCurrentFillPercentage(long currentHashCount) {
        return (currentHashCount * 100.0 / tableSize) <= minimumFillPercentage;
    }
}
