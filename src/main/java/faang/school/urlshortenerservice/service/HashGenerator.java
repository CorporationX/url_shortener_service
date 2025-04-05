package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Value("${hash.max-limit}")
    private int hashLimit;

    @Value("${hash.database.threshold}")
    private int minHashAmountInDatabase;

    @Transactional
    public void generateBatches() {
        if (hashRepository.count() >= minHashAmountInDatabase) {
            return;
        }
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashLimit);
            String[] hashes = encoder.generateHashes(uniqueNumbers);
            hashRepository.saveHashes(hashes);
    }
}
