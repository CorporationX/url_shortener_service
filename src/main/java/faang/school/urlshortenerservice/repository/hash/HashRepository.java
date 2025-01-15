package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;
import java.util.ArrayList;
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

    public List<Hash> save(List<Hash> hashes) {
       Iterable<Hash> iterable = hashJpaRepository.saveAll(hashes);
       List<Hash> result = new ArrayList<>();
       iterable.forEach(result::add);

       return result;
    }

    public List<Hash> getHashBatch() {
        return hashJpaRepository.getAndDeleteHashes(hashAmount);
    }
}
