package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateBatch(int n) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(n);
        List<String> encodedHashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(encodedHashes);
    }

    @Transactional
    public List<String> getBatch(int n) {
        List<String> hashes = hashRepository.getHashBatch(n);
        if (hashes.size() < n) {
            generateBatch(n);
            hashes.addAll(hashRepository.getHashBatch(n - hashes.size()));
        }
        return hashes;
    }
}