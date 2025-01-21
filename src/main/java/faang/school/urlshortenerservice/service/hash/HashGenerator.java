package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.properties.HashGeneratorProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashGeneratorProperties hashGeneratorProperties;

    @Transactional
    public List<String> getHashes(int amount) {
        return hashRepository.getAndDelete(amount).stream()
                .map(Hash::getHash)
                .toList();
    }

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateHashBatchAsync() {
        log.info("Starting async hash batch generation");
        generateHashBatch();
        log.info("Async batch hash generation completed");
    }

    @Transactional
    public void generateHashBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGeneratorProperties.getBatchSize());
        hashRepository.saveHashes(base62Encoder.encode(uniqueNumbers));
    }

    @Transactional(readOnly = true)
    public boolean isBelowMinimum() {
        int count = hashRepository.getHashesSize();
        log.info("Current hash count in database: {}", count);
        return count < hashGeneratorProperties.getMinLimit();
    }
}
