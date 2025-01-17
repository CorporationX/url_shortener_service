package faang.school.urlshortenerservice.repository.hash;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final HashJpaRepository hashJpaRepository;
    @Value("${hash.unique-numbers-amount}")
    private int uniqueNumbersAmount;
    @Value("${hash.amount}")
    private int hashAmount;

    public List<Long> getUniqueNumbers() {
        return hashJpaRepository.getNumbersFromSequence(uniqueNumbersAmount);
    }

    public void save(List<String> hashes) {
       hashJpaRepository.saveAll(hashes);
    }

    public List<String> getHashBatch() {
        return hashJpaRepository.getAndDeleteHashes(hashAmount);
    }
}
