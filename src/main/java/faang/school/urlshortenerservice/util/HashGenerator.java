package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch-get-unique-numbers-amount:10000}")
    private int hashGenerationAmount;

    @Value("${hash.min-hash-database-reserve:10000}")
    private long minHashDatabaseReserve;

    @Transactional
    @Async(value = "hashGeneratorThreadPool")
    public void generateBatch() {
        Long currentHashAmount = hashRepository.getCurrentHashAmount();
        if (currentHashAmount < minHashDatabaseReserve) {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGenerationAmount);
            List<String> hashes = base62Encoder.encode(uniqueNumbers);
            hashRepository.save(hashes);
        }
    }
}
