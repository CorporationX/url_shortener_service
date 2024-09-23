package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashRepository hashRepository;

    public List<String> getHashBatch(int batchSize) {
        List<String> hashBatch = hashRepository.getHashBatch(batchSize);
        log.info("Got hashBatch: {}", hashBatch);
        return hashBatch;
    }

    public void saveAllHashes(List<String> hashes) {
        List<Hash> hashEntities = hashes.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashEntities);
        log.info("Hashes saved to DB: {}", hashEntities);
    }

    public List<Long> getUniqueNumbers(int n) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
        log.info("{} unique numbers got from sequence \"unique_number_seq\": {}", n, uniqueNumbers);
        return uniqueNumbers;
    }
}
