package faang.school.urlshortenerservice.service.generator;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repositoy.HashJpaRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash-generator.unique-numbers.size}")
    private long uniqueNumbers;

    private final Base62Encoder base62Encoder;

    private final HashJpaRepository hashJpaRepository;

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashJpaRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashJpaRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    private void generateBatch() {
        hashJpaRepository.saveAll(base62Encoder.encode(hashJpaRepository.getUniqueNumbers(uniqueNumbers)));
    }
}
