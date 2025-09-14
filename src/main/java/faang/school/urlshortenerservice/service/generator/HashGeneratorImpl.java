package faang.school.urlshortenerservice.service.generator;


import faang.school.urlshortenerservice.common.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.generator.range}")
    private int maxRange;
    @Value("${hash.generator.max-retries}")
    private int maxRetries;
    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Override
    public List<String> generateBatch() {
        try {
            List<Long> uniqueNumbers = hashRepository.getHashNumbers(maxRange);
            if (uniqueNumbers.isEmpty()) {
                log.warn("No unique numbers generated for hash batch");
                return Collections.emptyList();
            }
            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            if (hashes.size() > batchSize) {
                hashes = hashes.subList(0, batchSize);
            }
            hashRepository.saveAll(
                    hashes.stream()
                            .map(Hash::new)
                            .collect(Collectors.toList())
            );
            return hashes;
        } catch (Exception e) {
            log.error("Error generating hash batch", e);
            throw new RuntimeException("Failed to generate hash batch", e);
        }
    }

    @Override
    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> generateBatchAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                for (int attempt = 1; attempt <= maxRetries; attempt++) {
                    try {
                        return generateBatch();
                    } catch (Exception e) {
                        if (attempt == maxRetries) {
                            throw e;
                        }
                        log.warn("Retry {}/{} for hash generation failed", attempt, maxRetries, e);
                        Thread.sleep(1000 * attempt); // экспоненциальная задержка
                    }
                }
                return Collections.emptyList();
            } catch (Exception e) {
                log.error("Final failure generating hash batch", e);
                throw new CompletionException(e);
            }
        });
    }
}