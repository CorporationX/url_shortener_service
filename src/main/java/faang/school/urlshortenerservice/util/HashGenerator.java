package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    @Value("${hash.hash-generator.generate-batch}")
    private int batch;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        log.info("Generating the batch of {} hashes", batch);
        List<Long> numbers = hashRepository.getUniqueNumbers(batch);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);
        log.info("The batch of {} hashes is generated and saved", batch);
    }
}
