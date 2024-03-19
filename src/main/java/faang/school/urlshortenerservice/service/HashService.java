package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    public List<Long> getNumbers(int maxRange) {
        return hashRepository.getUniqueNumbers(maxRange);
    }

    public List<Hash> save(List<Hash> hashes) {
        return hashRepository.saveAll(hashes);
    }

    public List<Hash> getAndDelete(int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }

    public void generateHash(int range) {
        hashGenerator.generateBatch(range);
    }
}
