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
    public void saveAllBatch(List<String> hashes) {
        hashJdbcRepository.saveAllBatch(hashes);
    }

    @Transactional
    public List<String> findAllByPackSize(int packSize) {
        return hashRepository.findAllAndDeletePack(packSize);
    }

    @Transactional
    public List<Long> getUniqueNumbers(int size) {
        return hashRepository.getUniqueNumbers(size);
    }
}
