package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final int BATCH_SIZE = 1000;

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

    public void saveUnusedHashes(List<String> hashes) {
        for (int i = 0; i < hashes.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, hashes.size());
            List<String> batch = hashes.subList(i, end);

            hashRepository.saveUnusedHashes(batch);
            log.info("Saved batch from {} to {}", i, end);
        }
    }
}

