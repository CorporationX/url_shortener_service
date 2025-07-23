package faang.school.urlshortenerservice.util.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final TaskExecutor hashGeneratorPool;

    @Value("${hash.generate_batch_size}")
    private final int generateBatchSize;

    @Override
    @Async("hashGeneratorPool")
    public void generateBatch() {
        try {
            log.info("Hashes Generation started");
            List<Long> numbers = hashRepository.getUniqueNumbers(generateBatchSize);
            if (numbers.isEmpty()) {
                log.warn("List of unique numbers is empty");
                return;
            }
            List<String> hashes = base62Encoder.encode(numbers);
            hashRepository.save(hashes);
            log.info("{} hashes was generated, from {} to {}",
                    hashes.size(),
                    numbers.get(0),
                    numbers.get(numbers.size() - 1));
        } catch (Exception e) {
            log.error("Failed to generate hashes for batchSize {}", generateBatchSize, e);
            throw new HashGenerationException("Failed to generate hashes", e);
        }
    }

    private class HashGenerationException extends RuntimeException {
        private HashGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}