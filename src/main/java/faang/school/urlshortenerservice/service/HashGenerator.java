package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.exeption.DataValidationException;
import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder base62Encoder;
    private final HashProperties hashProperties;
    private final TransactionTemplate transactionTemplate;

    @Async(value = "hashThreadPool")
    public void generateBatch() {
        int generationHashesSize = hashProperties.getBatchSizeForGenerationHashes();
        log.info("Starting batch generation with size {}", generationHashesSize);

        List<Long> uniqueNumbers = hashService.getUniqueNumbers(generationHashesSize);
        log.info("Generated {} unique numbers", uniqueNumbers.size());

        List<Hash> encodedHashes = base62Encoder.encodeList(uniqueNumbers);
        log.info("Encoded {} hashes", encodedHashes.size());

        try {
            transactionTemplate.execute(status -> {
                hashService.saveHashBatch(encodedHashes);
                log.info("Successfully saved {} hashes to database", encodedHashes.size());
                return null;
            });
        } catch (Exception e) {
            log.error("Error while saving hash batch to database: {}", e.getMessage(), e);
            throw new DataValidationException(e.getMessage());
        }
    }
}
