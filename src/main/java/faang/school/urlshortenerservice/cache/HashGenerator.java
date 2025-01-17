package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.config.HashGeneratorPropertiesConfig;
import faang.school.urlshortenerservice.entity.Hash;
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
    private final HashGeneratorPropertiesConfig hashGeneratorPropertiesConfig;
    private final Base62Encoder base62Encoder;

    public void generateBatch() {
        log.info("Starting hash generation with batch size: {}", hashGeneratorPropertiesConfig.batchSize());
        List<Long> uniqueNumbers = hashRepository.generateUniqueNumbers(hashGeneratorPropertiesConfig.batchSize());
        List<Hash> generatedHashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveAll(generatedHashes);
        log.info("Generated and save in hash repository {} unique hashes", generatedHashes.size());
    }
}
