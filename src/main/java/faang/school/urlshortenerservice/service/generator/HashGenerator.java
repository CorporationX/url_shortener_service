package faang.school.urlshortenerservice.service.generator;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repositoy.HashJpaRepository;
import faang.school.urlshortenerservice.repositoy.HashRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import faang.school.urlshortenerservice.service.encoder.Base62Encoder;

import java.util.List;

@Component
@Data
public class HashGenerator {

    @Value("${hash-generator.unique-numbers.size}")
    private long uniqueNumbers;

    private final HashRepository hashRepository;

    private final Base62Encoder base62Encoder;

    private final HashJpaRepository hashJpaRepository;

    @Async("getThreadPool")
    public void generateBatch(long uniqueNumbers) {
        hashRepository.save(base62Encoder.encode(hashRepository.getUniqueNumbers(uniqueNumbers)));
    }

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = hashJpaRepository.getHashBatch(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashJpaRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }
}
