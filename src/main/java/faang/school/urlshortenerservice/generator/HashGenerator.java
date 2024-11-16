package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.executor.ExecutorServiceConfig;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${uniqueNumbers.amount}")
    private int amount;

    @Value("${cache.batch.sublist.length}")
    private int sublistLength;

    private final ExecutorServiceConfig executorServiceConfig;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public List<Hash> getHashesForCache(int count) {
        log.info("start generateHashesForCache - count: {}", amount);
        List<Long> numbers = hashRepository.getUniqueNumbers(amount);

        List<Hash> hashes = base62Encoder.encode(numbers);
        hashRepository.saveAllHashesBatched(hashes);

        List<Hash> existingHashes = hashRepository.getHashBatch(count);
        log.info("finish generateHashesForCache with {} hashes", existingHashes);

        return existingHashes;
    }

    public List<Hash> getHashesBatch(int amount) {
        log.info("start getHashesBatch with amount: {}", amount);

        List<Hash> hashes = hashRepository.getHashBatch(amount);
        log.info("finish getHashesBatch with {} hashes", hashes.size());

        return hashes;
    }

    @Async("executor")
    @Transactional
    public void generateBatch() {
        log.info("start generateBatch - {}", Thread.currentThread().getName());

        List<Long> numbers = hashRepository.getUniqueNumbers(amount);
        log.info("amount of getUniqueNumbers - {}", numbers.size());

        List<CompletableFuture<List<Hash>>> futures = new ArrayList<>();
        for (int i = 0; i < numbers.size(); i += sublistLength) {
            List<Long> batch = numbers.subList(i, Math.min(i + sublistLength, numbers.size()));
            futures.add(encodeNumbers(batch));
        }

        CompletableFuture<List<Hash>> combinedFuture = CompletableFuture.completedFuture(new ArrayList<>());
        for (CompletableFuture<List<Hash>> future : futures) {
            combinedFuture = combinedFuture.thenCombine(future, (combinedHashes, batchHashes) -> {
                combinedHashes.addAll(batchHashes);
                return combinedHashes;
            });
        }

        combinedFuture.thenAccept(hashes -> {
            hashRepository.saveAllHashesBatched(hashes);
            log.info("finished generating batch with {} hashes.", hashes.size());
        });
    }

    @Transactional
    private CompletableFuture<List<Hash>> encodeNumbers(List<Long> numbers) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("encoding numbers in thread - {}", Thread.currentThread().getName());
            return base62Encoder.encode(numbers);
        }, executorServiceConfig.executor());
    }
}
