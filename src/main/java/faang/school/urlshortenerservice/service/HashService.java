package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;

    public void saveHashes(List<HashEntity> hashes) {
        hashRepository.saveAll(hashes);
    }

    public List<Long> getUniqueNumbers(int number) {
        return hashRepository.getUniqueNumbers(number);
    }

    @Transactional
    public List<String> getHashBatch(int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }
}

