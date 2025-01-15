package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.batch-size.unique-number}")
    private int uniqueNumberBatchSize;
    @Value("${hash.batch-size.save}")
    private int saveBatchSize;

    @Async("hashGeneratorThreadPool")
    @Transactional
    public void generateBatch() {
        log.info("Beginning of hash generation: {}", Thread.currentThread().getName());
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumberBatchSize);
        List<String> hashes = uniqueNumbers.stream()
                .map(base62Encoder::encode)
                .toList();
        hashRepository.save(hashes, saveBatchSize, jdbcTemplate);
        log.info("Successful saving of generated hashes: {}", Thread.currentThread().getName());
    }
}
