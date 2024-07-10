package faang.school.urlshortenerservice.service.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
