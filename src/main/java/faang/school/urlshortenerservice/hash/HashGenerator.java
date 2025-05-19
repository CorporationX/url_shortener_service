package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final HashProperties hashProperties;

    @Async("asyncExecutor")
    @Retryable(
            maxAttemptsExpression = "#{@retryProperties.maxAttempts()}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProperties.initialDelay()}",
                    maxDelayExpression = "#{@retryProperties.maxDelay()}",
                    multiplierExpression = "#{@retryProperties.multiplier()}"
            )
    )
    public void generateBatch() {
        try {
            log.info("Starting hash batch generation...");
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashProperties.getMaxRange());
            List<String> hashList = encoder.encode(uniqueNumbers);
            List<Hash> hashes = hashList.stream()
                    .map(hash -> Hash.builder().hash(hash).build())
                    .toList();
            hashRepository.saveAll(hashes);
            log.info("Saved {} new hashes to the database", hashes.size());
        } catch (Exception exception) {
            log.error("Failed to generate hash batch", exception);
            throw new HashGenerationException("Failed to generate hash batch");
        }
    }

    @Transactional
    public List<String> getHashes() {
        int batchSize = hashProperties.getBatchSize();
        List<Hash> hashes = hashRepository.deleteAndReturnUnusedHashesBatch(batchSize);

        if (hashes.size() < batchSize) {
            log.warn("Hash pool size ({}) is less than required batch size ({}). Triggering hash regeneration.",
                    hashes.size(), batchSize);
            generateBatch();
            hashes.addAll(hashRepository.deleteAndReturnUnusedHashesBatch(batchSize));
        }

        List<String> result = hashes.stream()
                .map(Hash::getHash)
                .toList();

        log.info("Returning {} hashes", result.size());
        return result;
    }
}
