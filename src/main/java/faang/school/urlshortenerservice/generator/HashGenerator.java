package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Value("${spring.url-shortener.hash.number-of-digits}")
    private long numberOfDigits;

    @Transactional
    public List<String> getHashes(int batchSize) {
        List<Hash> hashes = hashRepository.getHashBatch(batchSize);
        if (hashes.size() < batchSize) {
            generateHashes();
            hashes.addAll(hashRepository.getHashBatch(batchSize - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    private void generateHashes() {
        List<Long> numbers = hashRepository.getUniqueNumbers(numberOfDigits);
        List<Hash> hashes = encoder.encode(numbers);
        hashRepository.saveAll(hashes);
    }
}
