package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashJdbcRepository hashJdbcRepository;
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${app.hash_generator.get_unique_number_size}")
    private int numberSize;

    @Async("hashGeneratorPool")
    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(numberSize);
        List<String> hashes = encoder.encode(numbers);
        hashJdbcRepository.saveHashesByBatch(hashes);
    }
}
