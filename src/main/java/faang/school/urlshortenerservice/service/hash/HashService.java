package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashJdbcRepository hashJdbcRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void saveBatch(List<String> hashes) {
        hashJdbcRepository.saveHashesByBatch(hashes);
    }

    @Transactional
    public List<String> getHashesByBatchSize(int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }

    @Transactional
    public List<Long> getUniqueNumber(int size) {
        return hashRepository.getUniqueNumbers(size);
    }
}
