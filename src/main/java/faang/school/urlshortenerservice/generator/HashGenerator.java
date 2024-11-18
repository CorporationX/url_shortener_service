package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final ExecutorService hashGenerationExecutor;

    @Value("${hash.batch-size}")
    private int batchSize;

    public void generateHashesBatch(int amount) {
        int numBatches = (int) Math.ceil((double) amount / batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numBatches; i++) {
            int amountForBatch = Math.min(batchSize, amount - i * batchSize);
            futures.add(CompletableFuture.runAsync(() -> processBatch(amountForBatch), hashGenerationExecutor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("Generated a total of {} hashes in {} batches", amount, numBatches));
    }

    private void processBatch(int amount) {
        List<Long> numbers = hashRepository.getUniqueNumbers(amount);
        List<Hash> hashes = base62Encoder.encodeHashes(numbers);

        hashRepository.saveAllAndFlush(hashes);
        log.info("Generated and saved {} hashes in current batch", hashes.size());
    }

    public void shutdownExecutor() {
        hashGenerationExecutor.shutdown();
        try {
            if (!hashGenerationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                hashGenerationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            hashGenerationExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
