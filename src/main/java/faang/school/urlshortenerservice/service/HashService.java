package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.hash.Base62Encoder;
import faang.school.urlshortenerservice.model.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generate-size}")
    private int generateSize;

    @Value("${hash.low-threshold-free-hash-size}")
    private int lowThresholdFreeHashSize;

    @Value("${hash.hash-batch-size}")
    private int hashBatchSize;

    @Transactional
    public void generateHashIfNecessary() {
        long freeHashSize = hashRepository.count();
        if (freeHashSize > lowThresholdFreeHashSize) {
            return;
        }
        List<Long> uniqueNums = hashRepository.getUniqueNumbers(generateSize);
        List<HashEntity> hashEntities = base62Encoder.encode(uniqueNums).stream()
                .map(HashEntity::new)
                .toList();
        hashRepository.saveAll(hashEntities);
        log.info("Generated {} hashes", hashEntities.size());
    }

    @Transactional
    public List<String> getHashBatch() {
        List<HashEntity> randomHashEntity = hashRepository.getRandomHashBatch(hashBatchSize);
        hashRepository.deleteAll(randomHashEntity);
        return randomHashEntity.stream()
                .map(HashEntity::getHash)
                .toList();
    }
}
