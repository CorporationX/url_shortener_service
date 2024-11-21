package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${generator.batch.size}")
    private int batchSize;

    @Transactional
    private List<String> generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = encoder.encode(range);
        List<Hash> hashesEntity = encoder.encode(range).stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashesEntity);

        return hashes;
    }

    @Transactional
    public List<String> getHashes(int amount) {
        Long hashesSize = hashRepository.getHashesSize();

        if (hashesSize < amount) {
            return generateBatch();
        }

        return hashRepository.getHashBatch(amount);
    }
}
