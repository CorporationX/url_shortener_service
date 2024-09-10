package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hashes.amount}")
    private int hashesAmount;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(hashesAmount);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.saveHashes(hashes);
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.getAndDeleteHash(amount);
        if (hashes.size() < amount) {
            generateBatch();
            hashes = hashRepository.getAndDeleteHash(amount - hashes.size());
        }
        return hashes;
    }
}
