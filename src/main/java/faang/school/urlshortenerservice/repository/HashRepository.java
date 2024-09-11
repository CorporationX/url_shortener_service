package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final HashProperties properties;
    private final HashJpaRepository hashJpaRepository;

    public List<Long> getUniqueNumbers(long numbersCount) {
        return hashJpaRepository.getUniqueNumbers(numbersCount);
    }

    public void saveHashes(List<Hash> hashes) {
        int batchSize = properties.getBatchSize();
        IntStream.iterate(0, i -> i < hashes.size(), i -> i + batchSize)
                .mapToObj(i -> hashes.subList(i, Math.min(i + batchSize, hashes.size())))
                .forEach(hashJpaRepository::saveAll);
    }

    public List<Hash> getHashBatch() {
        return hashJpaRepository.getHashBatch(properties.getHashSize());
    }
}
