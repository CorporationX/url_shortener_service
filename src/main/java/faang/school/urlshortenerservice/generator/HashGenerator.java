package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash-generator.range:10000}")
    private int maxRange;

    @Value("${app.hash-generator.max-attempts:3}")
    private int maxAttempts;

    @Transactional
    @Scheduled(cron = "${app.hash-generator.cron}")
    public void generateHash() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<String> encodedHashes = base62Encoder.encode(range);
        List<Hash> hashes = encodedHashes.stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
        log.info("Generated and saved {} hashes", hashes.size());
    }

    @Transactional
    public List<String> getHashBatch(long amount) {
        List<Hash> hashes = new ArrayList<>();

        for (int attempt = 0; attempt <= maxAttempts; attempt++) {
            List<Hash> batch = hashRepository.getHashBatchAndDelete(amount - hashes.size());
            hashes.addAll(batch);

            if (hashes.size() >= amount) {
                break;
            }

            log.warn("Only {} hashes available, generating more (attempt {}/{})", hashes.size(), attempt + 1, maxAttempts);
            generateHash();
        }

        if (hashes.isEmpty()) {
            throw new IllegalStateException("No hashes available after retries");
        }

        if (hashes.size() < amount) {
            log.warn("Returned only {} hashes, less than requested {}", hashes.size(), amount);
        }

        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashBatchAsync(long amount) {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        try {
            List<String> hashes = getHashBatch(amount);
            future.complete(hashes);
        } catch (Exception e) {
            log.error("Failed to get hash batch asynchronously", e);
            future.completeExceptionally(e);
        }
        return future;
    }
}