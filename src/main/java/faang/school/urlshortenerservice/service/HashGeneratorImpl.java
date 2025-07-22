package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.HashGenerationException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateBatch() {
        try {
            List<Long> numbers = repository.getUniqueNumbers(numbersCount);
            log.debug("Generated {} unique numbers", numbers.size());

            List<String> hashBatch = base62Encoder.encode(numbers);
            log.debug("Encoded {} hashes", hashBatch.size());

            repository.saveBatch(hashBatch);
            log.info("Saved {} hashes to database", hashBatch.size());

        } catch (Exception e) {
            log.error("Error generating hash batch", e);
            throw new HashGenerationException("Failed to generate hash batch", e);
        }
    }
}
