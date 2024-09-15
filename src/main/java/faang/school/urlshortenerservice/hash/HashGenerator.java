package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    @Value("${hash.unique-numbers-size}")
    private int size;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Async(value = "taskExecutor")
    @Transactional
    @Scheduled(cron = "${scheduler.cron.hash_generator}")
    public void generateBatch() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(size);
            List<Hash> hashes = encoder.encodeList(uniqueNumbers);
            hashRepository.saveHashes(hashes);
            log.info("New hashes are generated: {}", hashes);
        } catch (Exception e) {
            log.error("Error occurred during hash generation or saving: {}", e.getMessage(), e);
            throw new HashGenerationException("Failed to generate hashes batch", e);
        }
    }
}
