package faang.school.urlshortenerservice.service.generator;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repositoy.HashJpaRepository;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Data
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder base62Encoder;

    private final HashJpaRepository hashJpaRepository;

    @Transactional
    public List<Hash> getHashes(int amount) {
        List<Hash> hashes = hashJpaRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch(amount);
            hashes.addAll(hashJpaRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    private void generateBatch(int uniqueNumbers) {
        hashJpaRepository.saveAll(base62Encoder.encodeAll(hashJpaRepository.getUniqueNumbers(uniqueNumbers)));
    }
}
