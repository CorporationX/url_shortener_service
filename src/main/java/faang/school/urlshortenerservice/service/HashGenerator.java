package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash-config.hash-count}")
    private int hashCount;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        log.info("Starting hash generation on thread: {}", Thread.currentThread().getName());
        if (!isBelowThreshold()) {
            log.info("Hash count is above the threshold; no hashes generated.");
            return;
        }
        List<Long> uniqueValues = hashRepository.getNextRange(hashCount);

        List<Hash> hashes = base62Encoder.encode(uniqueValues).stream()
                .map(Hash::new)
                .toList();
        log.info("Saving hashes to the database on thread: {}", Thread.currentThread().getName());
        hashRepository.saveAll(hashes);
        log.info("Hash generation completed successfully on thread: {}", Thread.currentThread().getName());
    }

    private boolean isBelowThreshold() {
        long currentHashCount = hashRepository.count();
        int threshold = (int) (0.2 * hashCount);
        return currentHashCount < threshold;
    }
}
