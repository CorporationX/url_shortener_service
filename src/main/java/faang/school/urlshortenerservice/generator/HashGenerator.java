package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.SequenceRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashService hashService;
    private final SequenceRepository sequenceRepository;

    @Value("${hash.batch.generation.size:10000}")
    private int batchSize;

    @Transactional
    public void generateBatch() {
        log.info("Starting generate {} hashes", batchSize);
        List<Long> numbers = sequenceRepository.getUniqueNumbers(batchSize);
        hashService.save(Base62Encoder.encodeNumbers(numbers));
        log.info("Successfully generated {} hashes", batchSize);
    }

    @Async("hashGeneratorPool")
    @Transactional
    public void generateBatchAsync() {
        generateBatch();
    }
}
