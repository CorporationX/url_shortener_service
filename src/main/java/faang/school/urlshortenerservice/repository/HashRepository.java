package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final HashProperties hashProperties;
    private final HashJpaRepository hashJpaRepository;

    public List<Long> getNUniqueNumbers(long numbersCount) {
        return hashJpaRepository.getNUniqueNumbers(numbersCount);
    }

    public void saveHashesList(List<@Valid Hash> hashes) {
        IntStream.iterate(0, i -> i < hashes.size(), i -> i + hashProperties.getSaveBatch())
                .mapToObj(i -> hashes.subList(i, Math.min(i + hashProperties.getSaveBatch(), hashes.size())))
                .forEach(hashJpaRepository::saveAll);
    }

    public List<Hash> getHashBatch() {
        return hashJpaRepository.getHashBatch(hashProperties.getGetBatch());
    }
}