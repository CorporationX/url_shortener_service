package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class HashGenerator {
    private final JdbcHashRepository jdbcHashRepository;
    private final Base62Encoder encoder;

    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Transactional
    public List<String> generateBatch() {
        List<Long> numbers = jdbcHashRepository.getNextNumbers(batchSize);
        List<String> hashes = encoder.encode(numbers);
        jdbcHashRepository.save(hashes);
        log.info("Generated and saved {} hashes", hashes.size());
        return hashes;
    }
}