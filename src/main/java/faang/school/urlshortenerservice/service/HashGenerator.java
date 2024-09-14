package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${unique-numbers.amount}")
    private int uniqueNumbersAmount;

    @Async(value = "hashTaskExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbersToEncode = hashRepository.getUniqueNumbers(uniqueNumbersAmount);
        List<String> hashesOfUniqueNumbers = base62Encoder.encode(uniqueNumbersToEncode);

        saveHashes(hashesOfUniqueNumbers);
    }

    @Transactional
    public void saveHashes(List<String> hashes) {
        hashRepository.save(hashes);
    }

    @Transactional
    public List<String> getHashes(int count) {
        if (hashRepository.getSize() <= count) {
            generateBatch();
        }

        return hashRepository.getHashes(count).stream()
                .map(Hash::getHash)
                .toList();
    }
}
