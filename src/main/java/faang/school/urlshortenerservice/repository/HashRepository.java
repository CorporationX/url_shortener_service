package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${hash.batch-size}")
    private int batchSize;
    @Value("${hash.hash-size}")
    private int hashSize;
    private final HashJpaRepository hashJpaRepository;

    public List<Long> getUniqueNumbers(long numbersCount) {
        return hashJpaRepository.getUniqueNumbers(numbersCount);
    }

    public void saveHashes(List<Hash> hashes) {
        IntStream.iterate(0, i -> i < hashes.size(), i -> i + batchSize)
                .mapToObj(i -> hashes.subList(i, Math.min(i + batchSize, hashes.size())))
                .forEach(hashJpaRepository::saveAll);
    }

    public List<Hash> getHashBatch() {
        return hashJpaRepository.getHashBatch(hashSize);
    }
}
