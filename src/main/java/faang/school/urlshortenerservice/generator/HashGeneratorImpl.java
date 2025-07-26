package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final Base62Encoder base62Encoder;
    private final HashRepository repository;
    @Value("${shortener.generator.unique-numbers-count}")
    private int numbersCount;

    @Override
    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatchAsync() {
        generateBatch();
    }

    @Override
    @Transactional
    public void generateBatch() {
        try {
            List<Long> numbers = repository.getUniqueNumbers(numbersCount);
            List<String> hashBatch = base62Encoder.encode(numbers);
            repository.saveBatch(hashBatch);
            log.info("Saved {} hashes to database", hashBatch.size());
        } catch (Exception e) {
            log.error("Error generating hash batch", e);
            throw new HashGenerationException("Failed to generate hash batch", e);
        }
    }
}
