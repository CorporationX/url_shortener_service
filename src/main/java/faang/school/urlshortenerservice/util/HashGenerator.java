package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder base62Encoder;
    private final HashProperties hashProperties;

    @Async(value = "hashTaskExecutor")
    public void generateBatch() {
        int generationHashesSize = hashProperties.getBatchSizeForGenerationHashes();
        log.info("Starting batch generation with size {}", generationHashesSize);

        List<Long> uniqueNumbers = hashService.getUniqueNumbers(generationHashesSize);
        log.info("Generated {} unique numbers", uniqueNumbers.size());

        List<Hash> encodedHashes = base62Encoder.encode(uniqueNumbers);
        log.info("Encoded {} hashes", encodedHashes.size());

        hashService.saveAllHashes(encodedHashes);
        log.info("Successfully saved {} hashes to database", encodedHashes.size());
    }
}
