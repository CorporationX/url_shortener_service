package faang.school.urlshortenerservice.service.hash;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.entity.hash.HashEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;

    @Value("${app.hash.batch-size}")
    private int batchSize;

    @Transactional
    public void saveHashes(List<String> hashes) {
        List<HashEntity> hashEntities = hashes.stream()
                .map(HashEntity::new)
                .toList();
        hashRepository.saveAll(hashEntities);
    }

    public List<Long> generateUniqueNumbers(int count) {
        return hashRepository.getUniqueNumbers(count);
    }

    @Transactional
    public List<String> retrieveAndDeleteHashes() {
        return hashRepository.getHashBatch(batchSize);
    }
}