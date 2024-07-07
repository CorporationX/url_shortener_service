package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Репозиторий для сохранения и получения хешей, а также уникальных сидов для генерации хешей
 */
@Repository
@RequiredArgsConstructor
public class HashRepository {
    @Value("${repository.save-batch-size}")
    private int saveBatch;
    @Value("${repository.get-batch-size}")
    private int getBatch;
    private final HashJpaRepository hashJpaRepository;


    //TODO: использовать в сервисе
    public List<Long> getNUniqueNumbers(long numbersCount) {
        return hashJpaRepository.getNUniqueNumbers(numbersCount);
    }

    //TODO: использовать в сервисе
    public void saveHashesList(List<Hash> hashes) {
        IntStream.iterate(0, i -> i < hashes.size(), i -> i + saveBatch)
                .mapToObj(i -> hashes.subList(i, Math.min(i + saveBatch, hashes.size())))
                .forEach(hashJpaRepository::saveAll);
    }

    //TODO: использовать в сервисе
    public List<Hash> getHashBatch() {
        return hashJpaRepository.getHashBatch(getBatch);
    }
}
