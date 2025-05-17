package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.config.app.HashGeneratorConfig;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.interfaces.HashRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@AllArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorConfig hashGeneratorConfig;
    private final ThreadPoolTaskExecutor hashGeneratorExecutor;

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Void> generateBatch() {
        int batchSize = hashGeneratorConfig.getBatchSize();
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);
        return CompletableFuture.completedFuture(null);
    }

    /*@Async("hashGeneratorExecutor")
    public void generateBatch() {
        int batchSize = hashGeneratorConfig.getBatchSize();
        try {
            List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
            List<String> hashes = base62Encoder.encode(numbers);
            hashRepository.save(hashes);
        } catch (Exception e) {
            throw new HashGenerationException("Failed to generate batch of " + batchSize + " hashes", e);
        }
    }*/

    public CompletableFuture<Void> generateHashes(int count) {
        log.info("Starting generation of {} hashes", count);
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive: " + count);
        }

        int batchSize = hashGeneratorConfig.getBatchSize();
        int batches = (int) Math.ceil((double) count / batchSize);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < batches; i++) {
            final int batchIndex = i;
            final int remaining = Math.min(batchSize, count - (batchIndex * batchSize));
            if (remaining <= 0) {
                break;
            }

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                int currentBatchSize = Math.min(batchSize, remaining);
                log.info("Generating batch {} of {} with {} hashes", batchIndex + 1, batches, currentBatchSize);
                int originalBatchSize = hashGeneratorConfig.getBatchSize();
                try {
                    hashGeneratorConfig.setBatchSize(currentBatchSize);
                    generateBatch();
                } finally {
                    hashGeneratorConfig.setBatchSize(originalBatchSize);
                }
            }, hashGeneratorExecutor);
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> log.info("Completed generation of {} hashes", count))
                .exceptionally(throwable -> {
                    throw new HashGenerationException("Failed to generate " + count + " hashes", throwable);
                });
    }
}
