package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

    @Value("${services.hash.batch.size}")
    private long batchSize;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Override
    @Transactional
    public void generateBatch() {

        List<Long> generatedValues = hashRepository.getUniqueNumbers(batchSize);
        List<String> encodedValues = base62Encoder.encode(generatedValues);
        hashRepository.saveAll(encodedValues);

        log.info("Generated new batch of hashes: {}", encodedValues);
    }

    @Override
    @Transactional
    public List<String> getBatch() {

        List<String> hashes = hashRepository.getHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(batchSize - hashes.size()));
        }
        return hashes;
    }
}
