package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;

    public List<Long> getUniqueNumbers(int n) {
        return hashRepository.getUniqueNumbers(n);
    }

    public List<String> getHashBatch(@Param("batchSize") int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }

    public void saveAllHashes(List<String> hashes) {
        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashEntities);
    }
}
