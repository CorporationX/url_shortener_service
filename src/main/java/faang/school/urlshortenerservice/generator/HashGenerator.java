package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.exception.InternalException;
import faang.school.urlshortenerservice.exception.ValidationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorProperties hashGeneratorProperties;

    @Async("hashGeneratorTaskExecutor")
    public void generateBatch() {
        int batchSize = hashGeneratorProperties.getBatchSize();

        if (batchSize <= 0) {
            throw new ValidationException("Batch size must be greater than zero");
        }

        log.info("Starting async hash generation, batchSize={}", batchSize);

        List<Long> uniqueNumbers;
        try {
            uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        } catch (Exception e) {
            throw new InternalException("Failed to get unique numbers from DB", e);
        }

        List<String> hashes;
        try {
            hashes = base62Encoder.encode(uniqueNumbers);
        } catch (Exception e) {
            throw new InternalException("Failed to encode unique numbers to base62", e);
        }

        try {
            hashRepository.save(hashes);
        } catch (Exception e) {
            throw new InternalException("Failed to save generated hashes to DB", e);
        }

        log.info("Successfully generated and saved {} hashes", hashes.size());
    }
}