package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.config.properties.hash.HashGeneratorProperties;
import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashBatchGeneratorImpl implements HashBatchGenerator {

    private final HashGeneratorProperties hashGeneratorProperties;
    private final HashRepository hashRepository;
    private final Encoder encoder;

    @Override
    @Transactional
    public List<String> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGeneratorProperties.batchSize());
        if (uniqueNumbers.isEmpty()) {
            log.info("generateBatch: no unique numbers fetched");
            return List.of();
        }
        List<String> hashes = encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes.stream()
                .map(Hash::new)
                .toList());
        log.info("generateBatch: generated {} hashes", hashes.size());
        return hashes;
    }
}
