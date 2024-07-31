package faang.school.urlshortenerservice.repositoy;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    @Value("${hash-repository.batch-size}")
    private int batchSize;

    private final HashJpaRepository hashJpaRepository;

    public void save(List<Hash> hashes) {
        int totalHashes = hashes.size();

        for (int startIndex = 0; startIndex < totalHashes; startIndex += batchSize) {
            int endIndex = Math.min(startIndex + batchSize, totalHashes);
            List<Hash> currentBatch = hashes.subList(startIndex, endIndex);
            hashJpaRepository.saveAll(currentBatch);
        }
    }
}
