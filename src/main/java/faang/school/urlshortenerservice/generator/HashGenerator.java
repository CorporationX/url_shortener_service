package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
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
        try {
            int batchSize = hashGeneratorProperties.getBatchSize();
            log.info("Starting async hash generation, batchSize={}", batchSize);

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.debug("Got {} unique numbers from DB", uniqueNumbers.size());

            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            log.debug("Encoded to base62, got {} hashes", hashes.size());

            hashRepository.save(hashes);

            log.info("Successfully generated and saved {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Error generating batch of hashes", e);
        }
    }
}

