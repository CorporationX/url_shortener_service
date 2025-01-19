package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final EntityManager entityManager;

    private static final int MIN_HASH_COUNT = 20;

    public void generateAndSaveHashes(int rangeSize) {
        long count = hashRepository.countHashes();

        if (count >= MIN_HASH_COUNT) {
            log.info("Sufficient number of hashes available, skipping generation.");
            return;
        }

        log.info("Generating {} new hashes", rangeSize);

        List<Long> uniqueNumbers = generateUniqueNumbers(rangeSize);

        List<String> hashes = base62Encoder.encode(uniqueNumbers);

        // Check for hash and uniqueNumber size mismatch
        if (hashes.size() != uniqueNumbers.size()) {
            log.error("Mismatch between the number of hashes and unique numbers generated.");
            throw new IllegalStateException("Hash generation failed due to size mismatch");
        }

        for (int i = 0; i < hashes.size(); i++) {
            Hash hash = new Hash(uniqueNumbers.get(i), hashes.get(i));
            hashRepository.save(hash);
        }

        log.info("Successfully generated and saved {} hashes", hashes.size());
    }

    private List<Long> generateUniqueNumbers(int rangeSize) {
        List<Long> uniqueNumbers = new ArrayList<>();

        for (int i = 0; i < rangeSize; i++) {
            Long uniqueNumber = getNextUniqueNumber();
            uniqueNumbers.add(uniqueNumber);
        }

        return uniqueNumbers;
    }

    public Long getNextUniqueNumber() {
        try {
            Query query = entityManager.createNativeQuery("SELECT nextval('unique_number_seq')");
            return ((Number) query.getSingleResult()).longValue();
        } catch (Exception e) {
            log.error("Error fetching the next unique number from the sequence: {}", e.getMessage());
            throw new RuntimeException("Error fetching unique number", e);
        }
    }
}
