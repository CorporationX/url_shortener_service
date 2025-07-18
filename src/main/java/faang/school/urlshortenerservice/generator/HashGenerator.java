package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashDao;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashDao hashDao;

    @Transactional
    public List<String> generateBatch(int hashesToGenerate) {
        try {
            List<Long> uniqueNumbers = hashDao.getUniqueNumbers(hashesToGenerate);
            if (uniqueNumbers.isEmpty()) {
                log.info("No unique numbers were fetched. Skipping hash generation.");
                return Collections.emptyList();
            }
            log.info("Fetched {} unique numbers from the database.", uniqueNumbers.size());

            List<String> newHashes = Base62Encoder.encode(uniqueNumbers);
            hashDao.save(newHashes);
            log.info("Successfully generated and stored {} new hashes.", newHashes.size());
            return newHashes;
        } catch (Exception e) {
            log.error("Failed to generate and store new hashes", e);
            return Collections.emptyList();
        }
    }
}
