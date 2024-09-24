package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    @Value("${hashes.amount}")
    private int hashesAmount;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    public List<String> generateBatch() {
        log.info("Generating hashes for batch");
        List<Long> numbers = hashRepository.getUniqueNumbers(hashesAmount);
        log.info("got {} Number of unique numbers", numbers.size());
        List<String> hashes = base62Encoder.encode(numbers);
        log.info("{} numbers were encoded to base62", hashes.size());
        return hashes;
    }

    public List<String> generateBatch(int batchSize) {
        log.info("Generating hashes for batch");
        List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
        log.info("got {} Number of unique numbers", numbers.size());
        List<String> hashes = base62Encoder.encode(numbers);
        log.info("{} numbers were encoded to base62", hashes.size());
        return hashes;
    }

    public void generateAndSaveHashes() {
        List<String> hashes = generateBatch();
        hashRepository.saveHashes(hashes);
        log.info("{} hashes were saved", hashes.size());
    }

    @Transactional
    public List<String> getHashes(int amount) {
        List<String> hashes = hashRepository.getAndDeleteHashes(amount);
        log.info("{} hashes were extracted from Database", hashes.size());
        if (hashes.size() < amount) {
            log.info("there were not enough hashes in database");
            List<String> additionalHashes = generateBatch(amount - hashes.size());
            hashes.addAll(additionalHashes);
            log.info("missing hashes were extracted from Database");
        }
        return hashes;
    }
}
