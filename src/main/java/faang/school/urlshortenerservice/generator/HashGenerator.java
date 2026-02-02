package faang.school.urlshortenerservice.generator;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashJdbcRepository hashJdbcRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch-size}")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        log.info("Generating hash batch, size={}", batchSize);

        List<Long> numbers = hashJdbcRepository.getUniqueNumbers(batchSize);
        List<String> hashes = base62Encoder.encode(numbers);
        hashJdbcRepository.save(hashes);

        log.info("Successfully generated {} hashes", hashes.size());
    }
    
}
