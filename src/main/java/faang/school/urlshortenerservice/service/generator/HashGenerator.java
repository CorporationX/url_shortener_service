package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash.batch-size}")
    private int batchSize;

    @Setter
    @Value("${app.hash.generator.enabled}")
    private boolean enabled;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        if (!enabled) {
            log.info("Hash generator is disabled");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            log.info("Starting async hash generation batch of size: {}", batchSize);

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            if (uniqueNumbers == null || uniqueNumbers.isEmpty()) {
                log.error("Failed to get unique numbers from database");
                return;
            }
            List<String> hashes = base62Encoder.encodeBatch(uniqueNumbers);
            if (hashes == null || hashes.isEmpty()) {
                log.error("Failed to encode unique numbers to hashes");
                return;
            }
            hashRepository.save(hashes);
            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully generate and saved {} new hashes in {} ms", hashes.size(), duration);

        } catch (Exception e) {
            log.error("Failed to generate hash batch", e);
            throw new RuntimeException("Hash generation failed",e);
        }
    }

    public void generateBatchSync() {
        if (!enabled) {
            log.info("Hash generator is disabled");
            return;
        }

        long startTime = System.currentTimeMillis();

        try {
            log.info("Starting synchronous hash generation batch of size: {}", batchSize);

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            if (uniqueNumbers == null || uniqueNumbers.isEmpty()) {
                log.error("Failed to get unique numbers from database");
                return;
            }

            List<String> hashes = base62Encoder.encodeBatch(uniqueNumbers);
            if (hashes == null || hashes.isEmpty()) {
                log.error("Failed to encode numbers to hashes");
                return;
            }

            hashRepository.save(hashes);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully generated and saved {} new hashes synchronously in {} ms",
                    hashes.size(), duration);

        } catch (Exception e) {
            log.error("Failed to generate hash batch synchronously", e);
            throw new RuntimeException("Synchronous hash generation failed", e);
        }
    }

    public List<String> generateBatchSyncAndReturn() {
        if (!enabled) {
            log.info("Hash generator is disabled");
            return Collections.emptyList();
        }

        long startTime = System.currentTimeMillis();

        try {
            log.info("Starting synchronous hash generation batch of size: {}", batchSize);

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            if (uniqueNumbers == null || uniqueNumbers.isEmpty()) {
                log.error("Failed to get unique numbers from database");
                return Collections.emptyList();
            }

            List<String> hashes = base62Encoder.encodeBatch(uniqueNumbers);
            if (hashes == null || hashes.isEmpty()) {
                log.error("Failed to encode numbers to hashes");
                return Collections.emptyList();
            }

            hashRepository.save(hashes);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Successfully generated and saved {} new hashes synchronously in {} ms",
                    hashes.size(), duration);

            return hashes;

        } catch (Exception e) {
            log.error("Failed to generate hash batch synchronously", e);
            throw new RuntimeException("Synchronous hash generation failed", e);
        }
    }
}
