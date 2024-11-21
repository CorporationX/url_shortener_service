package faang.school.urlshortenerservice.util.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final Encoder encoder;
    private final HashRepository hashRepository;

    @Setter
    @Value("${hash.generator.batch-size}")
    private int batchSize;

    @Async("taskExecutor")
    public void generateBatch() {
        List<Long> sequence = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = encoder.encodeBatch(sequence);
        hashRepository.save(hashes);
        log.info("Generated {} hashes", hashes.size());
    }
}
