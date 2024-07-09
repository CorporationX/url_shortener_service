package faang.school.urlshortenerservice.repositoy;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    @Value("${repository.save-batch.size}")
    private long saveBatch;

    @Value("${repository.get-batch.size}")
    private long getBatch;

    private final HashJpaRepository hashJpaRepository;

    public List<Long> getUniqueNumbers(long saveBatch) {
        return hashJpaRepository.getUniqueNumbers(saveBatch);
    }

    public void save(List<Hash> hashes) {
        hashJpaRepository.saveAll(hashes);
    }

    public List<Hash> getHashBatch(long getBatch) {
        return hashJpaRepository.getHashBatch(getBatch);
    }
}
