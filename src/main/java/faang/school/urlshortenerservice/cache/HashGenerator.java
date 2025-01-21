package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.HashGeneratorProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.exception.HashRetrievalException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashGeneratorProperties hashGeneratorProperties;
    private final Base62Encoder base62Encoder;

    public void generateAndSaveHashes() {
        try {
            log.info("Starting hash generation with batch maxCacheSize: {}", hashGeneratorProperties.hashBatchSize());
            List<Long> uniqueNumbers = hashRepository.generateUniqueNumbers(hashGeneratorProperties.hashBatchSize());
            List<Hash> generatedHashes = base62Encoder.encode(uniqueNumbers);
            hashRepository.saveAll(generatedHashes);
            log.info("Generated and saved in hash repository {} unique hashes", generatedHashes.size());
        } catch (Exception e) {
            throw new HashRetrievalException("Hash generation error occurred: " + e.getMessage());
        }
    }
}
